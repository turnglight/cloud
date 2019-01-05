package com.cloud.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service(value = "consumerService")
public class ConsumerService {

    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "helloFallback")
    public String helloConsumer(){
        return restTemplate.getForEntity("http://HELLO-SERVICE/hello",String.class).getBody();
    }

    public String helloFallback(){
        return "error";
    }

}
