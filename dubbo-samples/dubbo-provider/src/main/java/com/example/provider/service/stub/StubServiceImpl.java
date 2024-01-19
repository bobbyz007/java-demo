package com.example.provider.service.stub;

import com.example.client.stub.StubService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(group = "dg", version = "1.0.0")
public class StubServiceImpl implements StubService {
    @Override
    public String sayHello(String name) {
        return "greeting " + name;
    }
}
