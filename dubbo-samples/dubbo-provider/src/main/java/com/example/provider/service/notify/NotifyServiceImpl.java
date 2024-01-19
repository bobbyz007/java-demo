package com.example.provider.service.notify;

import com.example.client.notify.NotifyService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(group = "dg", version = "1.0.0")
public class NotifyServiceImpl implements NotifyService {
    @Override
    public String sayHello(int id) {
        if (id > 10) {
            throw new RuntimeException("exception from sayHello: too large id");
        }
        return "demo" + id;
    }
}
