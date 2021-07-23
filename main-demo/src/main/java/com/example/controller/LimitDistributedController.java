package com.example.controller;

import com.example.concurrent.limit.distribute.spring.annotation.MyDistributedRateLimiter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 演示分布式环境下的限流： 不仅仅局限于单机器部署下应用的限流
 */
@RestController
@RequestMapping("/limit/distributed")
public class LimitDistributedController {
    @MyDistributedRateLimiter(key = "test", time = 10, count = 10)
    @GetMapping("/test")
    public String testLimit() {
        return "Hello,ok";
    }
}
