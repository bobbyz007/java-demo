package com.example.provider.service;

import com.example.client.GreetingService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(group = "dg", version = "1.0.0")
public class GreetingServiceImpl implements GreetingService {
    @Override
    public String hello() {
        return "Greetings";
    }
}
