package com.example.consumer.controller;

import com.example.client.DemoService;
import com.example.client.GreetingService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("dubboconsumer")
public class DubboConsumerController {
    @DubboReference(group = "dg", version = "1.0.0")
    private DemoService demoService;

    @DubboReference(group = "dg", version = "1.0.0")
    private GreetingService greetingService;

    @GetMapping("/hello")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return demoService.sayHello(name) + " ########### " + greetingService.hello();
    }
}
