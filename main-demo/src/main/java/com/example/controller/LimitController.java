package com.example.controller;

import com.example.concurrent.limit.spring.annotation.RateLimit;
import com.example.demo.consumingrest.Greeting;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 演示限流
 */
@RestController
@RequestMapping("/limit")
public class LimitController {
    @RateLimit(key = "test", time = 10, count = 10)
    @GetMapping("/test")
    public String testLimit() {
        return "Hello,ok";
    }
}
