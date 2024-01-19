package com.example.consumer.stub;

import com.example.client.stub.StubService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客户端也执行部分逻辑，指定stub实现
 */
@RestController
@RequestMapping("stub")
public class StubController {
    private static Logger logger = LoggerFactory.getLogger(StubController.class);

    // stub指定stub实现， stub实现必须有构造函数传入真正的远程代理对象
    @DubboReference(group = "dg", version = "1.0.0", check=false, stub="com.example.client.stub.StubServiceStub")
    private StubService demoService;

    @GetMapping("/get")
    public String call() throws InterruptedException {
        String result = demoService.sayHello("world");
        logger.info(result);
        return result;
    }
}
