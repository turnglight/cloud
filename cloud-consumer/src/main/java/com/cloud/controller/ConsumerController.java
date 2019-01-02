package com.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ConsumerController {

    @Autowired
    private RestTemplate restTemplate;


    @GetMapping("/ribbon-consumer")
    public ResponseEntity helloConsumer(){
        return restTemplate.getForEntity("http://HELLO-SERVICE/hello",String.class);
    }

}
