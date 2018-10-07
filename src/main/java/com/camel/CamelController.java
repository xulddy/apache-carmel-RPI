package com.camel;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CamelController {

    @Autowired
    ProducerTemplate producerTemplate;


    @RequestMapping(value = "/abc")
    public String startCamel() {
        producerTemplate.sendBody("direct:firstRoute", "Calling via Spring Boot Rest Controller");
        return "abc";
    }
}
