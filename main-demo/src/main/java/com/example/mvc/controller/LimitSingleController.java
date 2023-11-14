package com.example.mvc.controller;

import com.example.concurrent.limit.app.spring.annotation.MySingleRateLimiter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 演示限流：适用单机部署环境
 */
@RestController
@RequestMapping("/limit/single")
public class LimitSingleController {
    @MySingleRateLimiter(rate = 1.0, timeout = 500)
    @GetMapping("/test")
    public String testLimit() {
        return "Hello,ok: single app limiter";
    }
}
