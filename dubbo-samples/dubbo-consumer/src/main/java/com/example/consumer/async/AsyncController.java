package com.example.consumer.async;

import com.example.client.GreetingService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("async")
public class AsyncController {
    @DubboReference(group = "dg", version = "1.0.0")
    private GreetingService greetingService;

    @GetMapping("/hello")
    public String greeting() {
        RpcContext.getClientAttachment().setAttachment("consumer-key1", "consumer-value1");
        // 此处堵塞式调用。 但远程服务提供者的dubbo线程是异步(方法立即返回)， 并交给用户线程执行，这样不会堵塞占用dubbo线程池
        String bizResult = greetingService.helloAsync();
        return String.format("biz result: %s", bizResult);
    }
}
