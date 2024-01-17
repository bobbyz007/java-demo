package com.example.provider.service;

import com.example.client.EmbedService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(group = "dg", version = "1.0.0")
public class EmbedServiceImpl implements EmbedService {
    @Override
    public String biz() {
        return "embed service";
    }
}
