package com.example.consumer.generic;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.service.GenericService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * 消费者泛化， 提供者基于接口暴露服务
 */
@RestController
@RequestMapping("generic-c")
public class GenericConsumerController {
    private static Logger logger = LoggerFactory.getLogger(GenericConsumerController.class);

    // 消费者泛化，interfaceName需要指定提供者实现的接口
    @DubboReference(group = "dg", version = "1.0.0", timeout = 10000, interfaceName ="com.example.client.generic.consumer.HelloService")
    private GenericService genericService;

    @GetMapping("/get")
    public String call() throws InterruptedException {
        invokeSayHello();
        invokeSayHelloAsync();

        // GenericService.asyncInvoke 如果方法本身返回 CompletableFuture，则不会再用CompletableFuture包装
        asyncInvokeSayHello();
        asyncInvokeSayHelloAsyncComplex();
        asyncInvokeSayHelloAsyncGenericComplex();

        return this.getClass().getSimpleName();
    }

    private void invokeSayHello() throws InterruptedException {
        Object result = genericService.$invoke("sayHello", new String[]{"java.lang.String"}, new Object[]{"world"});
        logger.info("invokeSayHello(return): " + result);
    }

    private void invokeSayHelloAsync() throws InterruptedException {
        Object result = genericService.$invoke("sayHelloAsync", new String[]{"java.lang.String"}, new Object[]{"world"});
        CountDownLatch latch = new CountDownLatch(1);

        CompletableFuture<String> future = RpcContext.getServerContext().getCompletableFuture();
        future.whenComplete((value, t) -> {
            logger.info("invokeSayHelloAsync(whenComplete): " + value);
            latch.countDown();
        });

        latch.await();
    }

    /**
     * 使用GenericService的invokeAsync 方法包装
     */
    private void asyncInvokeSayHello() throws InterruptedException {
        CompletableFuture<Object> future = genericService.$invokeAsync("sayHello",
                new String[]{"java.lang.String"}, new Object[]{"world"});
        CountDownLatch latch = new CountDownLatch(1);
        future.whenComplete((value, t) -> {
            logger.info("asyncInvokeSayHello(whenComplete): " + value);
            latch.countDown();
        });
        latch.await();
    }

    /**
     * 使用GenericService的invokeAsync 方法包装
     */
    private void asyncInvokeSayHelloAsyncComplex() throws InterruptedException {
        CompletableFuture<Object> future = genericService.$invokeAsync("sayHelloAsyncComplex",
                new String[]{"java.lang.String"}, new Object[]{"world"});
        CountDownLatch latch = new CountDownLatch(1);

        future.whenComplete((value, t) -> {
            logger.info("asyncInvokeSayHelloAsyncComplex(whenComplete): " + value);
            latch.countDown();
        });

        latch.await();
    }

    /**
     * 使用GenericService的invokeAsync 方法包装
     */
    private void asyncInvokeSayHelloAsyncGenericComplex() throws InterruptedException {
        CompletableFuture<Object> future = genericService.$invokeAsync("sayHelloAsyncGenericComplex",
                new String[]{"java.lang.String"}, new Object[]{"world"});
        CountDownLatch latch = new CountDownLatch(1);

        future.whenComplete((value, t) -> {
            logger.info("asyncInvokeSayHelloAsyncGenericComplex(whenComplete): " + value);
            latch.countDown();
        });

        latch.await();
    }
}
