package com.example.mvc.service;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import org.springframework.stereotype.Component;

@Component
public class NacosConfigService {
    @NacosConfigListener(dataId = "test-data")
    public void onPropertyChanged(String msg) {
        System.out.println("changed: " + msg);
    }
}
