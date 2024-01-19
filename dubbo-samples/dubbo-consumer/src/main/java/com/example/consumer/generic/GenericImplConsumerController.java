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
 * 消费者泛化， 提供者也泛化。 此时interfaceName可以约定为任何值，无需实际存在的接口名称
 */
@RestController
@RequestMapping("generic-ci")
public class GenericImplConsumerController {
    private static Logger logger = LoggerFactory.getLogger(GenericImplConsumerController.class);

    @DubboReference(group = "dg", version = "1.0.0", interfaceName = "any.name.xxx")
    private GenericService genericService;

    @GetMapping("/get")
    public String callWithGeneric() throws InterruptedException {
        Object result = genericService.$invoke("sayHello", new String[]{"java.lang.String"}, new Object[]{"world"});
        logger.info("invokeSayHello(return): " + result);

        CompletableFuture<Object> future = genericService.$invokeAsync("sayHelloAsync",
                new String[]{"java.lang.String"}, new Object[]{"world"});
        CountDownLatch latch = new CountDownLatch(1);
        future.whenComplete((value, t) -> {
            logger.info("invokeSayHelloAsync(whenComplete): " + value);
            latch.countDown();
        });
        latch.await();

        return this.getClass().getSimpleName();
    }
}
