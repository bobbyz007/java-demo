package com.example.consumer.callback;

import com.example.client.GreetingService;
import com.example.client.callback.CallbackService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 当调用的是Callback方法时，Consumer端发送请求的同时暴露一个回调参数的服务，
 * 这样Provider返回结果的方式就变成了调用Consumer暴露的这个服务，也就是返回结果时Provider变成了Consumer。
 * 总结：基于长连接生成反向代理，这样就可以从服务器端调用客户端逻辑。
 */
@RestController
@RequestMapping("callback")
public class CallbackController {
    private static Logger logger = LoggerFactory.getLogger(CallbackController.class);

    @DubboReference(group = "dg", version = "1.0.0")
    private CallbackService callbackService;

    @GetMapping("/get")
    public String call() throws InterruptedException {
        callbackService.biz("foo", msg -> logger.info(msg));
        return "OK";
    }
}
