package com.example.consumer.async;

import com.example.client.GreetingService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * spring container 初始化该bean后即执行，演示async的调用方法
 */
@Component
public class AsyncRunner implements CommandLineRunner {
    private static Logger logger = LoggerFactory.getLogger(AsyncRunner.class);

    @DubboReference(group = "dg", version = "1.0.0")
    private GreetingService greetingService;

    @Override
    public void run(String... args) throws Exception {
        // 先调用远程接口，才能获取到Context
        greetingService.hello();
        CompletableFuture<String> helloFuture = RpcContext.getServerContext().getCompletableFuture();
        helloFuture.whenComplete((retValue, exception) -> {
            if (exception == null) {
                logger.info("return value: " + retValue);
            } else {
                exception.printStackTrace();
            }
        });

        // 非堵塞调用
        CompletableFuture<String> f = RpcContext.getServerContext().asyncCall(() -> greetingService.hello());
        logger.info("async call returned: " + f.get());
        RpcContext.getServerContext().asyncCall(() -> {
            greetingService.hello();
        });
    }
}
