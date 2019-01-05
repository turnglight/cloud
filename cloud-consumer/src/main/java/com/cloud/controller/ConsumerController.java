package com.cloud.controller;

import com.cloud.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {

    @Autowired
    private ConsumerService consumerService;

    @GetMapping("/ribbon-consumer")
    public ResponseEntity helloConsumer(){
        return ResponseEntity.ok(consumerService.helloConsumer());
    }

}
