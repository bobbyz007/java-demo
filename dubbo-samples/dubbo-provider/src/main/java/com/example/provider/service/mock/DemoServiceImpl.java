package com.example.provider.service.mock;

import com.example.client.mock.DemoService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(group = "dg", version = "1.0.0")
public class DemoServiceImpl implements DemoService {
    @Override
    public String sayHello(String name) {
        try {
            // sleeping 3 seconds leads to TimeoutException on client side, and mock impl will be invoked
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello " + name;
    }
}
