package com.example.mvc.service;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.stereotype.Component;

@Component
@NacosPropertySource(dataId = "test-data", autoRefreshed = true, type = ConfigType.PROPERTIES)
public class NacosConfigService {
    @NacosConfigListener(dataId = "test-data")
    public void onPropertyChanged(String msg) {
        System.out.println("changed: " + msg);
    }
}
