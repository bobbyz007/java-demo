package com.example.consumer.basic;

import com.example.client.GreetingService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * testing: http://localhost:8083/dubboconsumer/hello
 */
@RestController
@RequestMapping("consumer")
public class ConsumerController {
    @DubboReference(group = "dg", version = "1.0.0")
    private GreetingService greetingService;

    @GetMapping("/hello")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "########### " + greetingService.hello();
    }
}
