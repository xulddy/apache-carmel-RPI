package com.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;

@Component
public class RestApi extends RouteBuilder {

    @Value("${server.port}")
    String serverPort;

    @Value("${api.path}")
    String contextPath;

    @Override
    public void configure() throws Exception {
//        CamelContext context = new DefaultCamelContext();
        // http://localhost:8080/camel/api-doc
//        restConfiguration().contextPath(contextPath) //
//                .port(serverPort)
//                .enableCORS(true)
//                .apiContextPath("/api-doc")
//                .apiProperty("api.title", "Test REST API")
//                .apiProperty("api.version", "v1")
//                .apiProperty("cors", "true") // cross-site
//                .apiContextRouteId("doc-api")
//                .component("servlet")
//                .bindingMode(RestBindingMode.json)
//                .dataFormatProperty("prettyPrint", "true");

        from("direct:firstRoute")
                .log("Camel body: ${body}");


        rest("/api/")
                .description("Teste REST Service")
                .id("api-route")
                .post("/bean")
                .produces(MediaType.APPLICATION_JSON)
                .consumes(MediaType.APPLICATION_JSON)
//                .get("/hello/{place}")
                .bindingMode(RestBindingMode.auto)
                .type(MyBean.class)
                .enableCORS(true)
//                .outType(OutBean.class)
                .to("direct:remoteService")
                .id("api-rout2")
                .get("hello/{id}")
                .consumes(MediaType.APPLICATION_JSON)
                .bindingMode(RestBindingMode.auto)
                .type(MyBean.class)
                .enableCORS(true)
                .to("direct:remoteService2");;


        from("direct:remoteService2")
                .routeId("direct-route2")
//                .transform().simple("blue ");
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        String id = exchange.getIn().getHeader("id").toString();
                        exchange.getIn().setBody(id);
                    }
                });
        from("direct:remoteService")
                .routeId("direct-route")
                .tracing()
                .log(">>> ${body.id}")
                .log(">>> ${body.name}")
//                .transform().simple("blue ${in.body.name}")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        MyBean bodyIn = (MyBean) exchange.getIn().getBody();

                        ExampleServices.example(bodyIn);

                        exchange.getIn().setBody(bodyIn);
                    }
                })
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201));



    }
}
