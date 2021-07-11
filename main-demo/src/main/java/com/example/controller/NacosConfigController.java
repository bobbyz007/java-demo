package com.example.controller;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@NacosPropertySource(dataId = "test-data", groupId = "DEFAULT_GROUP",   autoRefreshed = true)
@RequestMapping("nacosconfig")
public class NacosConfigController {
    @NacosValue(value = "${key1:default}", autoRefreshed = true)
    private String value1;

    /**
     * @Value是从Spring容器中的Environment中获取对应的属性值,但是在启动Nacos的时候就把数据给加载到了Environment中去了;
     * 所以通过@Value也能获取到属性值; 但是它跟@NacosValue的区别是, 它不能够实时刷新数据; 它的值一直都是启动时候第一遍加载的数据
     */
    @Value(value = "${key1:staticdefault}")
    private String value11;

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public String get() {
        return "value1: " + value1 + ", value11: " + value11;
    }
}
