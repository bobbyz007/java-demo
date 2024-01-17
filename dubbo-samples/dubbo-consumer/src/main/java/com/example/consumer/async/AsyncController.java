package com.example.consumer.async;

import com.example.client.GreetingService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.rpc.RpcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;

@RestController
@RequestMapping("async")
public class AsyncController {
    private static Logger logger = LoggerFactory.getLogger(AsyncController.class);

    @DubboReference(group = "dg", version = "1.0.0")
    private GreetingService greetingService;

    @GetMapping("/hello")
    public String greeting() {
        RpcContext.getClientAttachment().setAttachment("consumer-key1", "consumer-value1");
        // 此处堵塞式调用。 但远程服务提供者的dubbo线程是异步(方法立即返回)， 并交给用户线程执行，这样不会堵塞占用dubbo线程池
        String bizResult = greetingService.helloAsync();
        return String.format("biz result: %s", bizResult);
    }

    @GetMapping("/future")
    public String future() throws InterruptedException {
        // 往服务端传递参数
        RpcContext.getClientAttachment().setAttachment("consumer-key1", "consumer-value1");

        CompletableFuture<String> f = greetingService.helloFuture();
        CountDownLatch latch = new CountDownLatch(1);
        f.whenComplete((v, t) -> {
            // TODO can't get attachment from server
            logger.info(RpcContext.getServerContext().getAttachment("server-key1"));

            if (t != null) {
                logger.warn("Exception: ", t);
            } else {
                logger.info("Response: " + v);
            }
            latch.countDown();
        });

        logger.info("Executed before response returns");
        latch.await();
        return "future completes";
    }
}
