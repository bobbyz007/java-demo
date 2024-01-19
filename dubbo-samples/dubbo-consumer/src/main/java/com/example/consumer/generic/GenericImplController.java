package com.example.consumer.generic;

import com.example.client.generic.impl.HelloService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.service.GenericService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * 提供者泛化， 消费者基于接口调用
 */
@RestController
@RequestMapping("generic-i")
public class GenericImplController {
    private static Logger logger = LoggerFactory.getLogger(GenericImplController.class);

    @DubboReference(group = "dg", version = "1.0.0")
    private HelloService helloService;

    @GetMapping("/get")
    public String call() throws InterruptedException {
        logger.info(helloService.sayHello("world"));

        CountDownLatch latch = new CountDownLatch(1);
        CompletableFuture<String> future = helloService.sayHelloAsync("world async");
        future.whenComplete((v, t) -> {
            logger.info(v);
            latch.countDown();
        });
        latch.await();

        return this.getClass().getSimpleName();
    }
}
