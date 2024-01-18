package com.example.consumer.direct;

import com.example.client.direct.DirectService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 绕过注册中心指定url 直连 provider
 */
@RestController
@RequestMapping("direct")
public class DirectController {
    private static Logger logger = LoggerFactory.getLogger(DirectController.class);

    // 指定url直连 某一个provider，hessian不兼容jdk 21，序列化修改为fastjson2
    @DubboReference(group = "dg", version = "1.0.0", url = "dubbo://127.0.0.1:20881?serialization=fastjson2")
    private DirectService directService;

    @GetMapping("/get")
    public String call() {
        return directService.hello("world");
    }
}
