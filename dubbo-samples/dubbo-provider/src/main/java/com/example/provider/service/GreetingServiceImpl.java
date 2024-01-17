package com.example.provider.service;

import com.example.client.EmbedService;
import com.example.client.GreetingService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.rpc.AsyncContext;
import org.apache.dubbo.rpc.RpcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DubboService(group = "dg", version = "1.0.0", methods = {@Method(name = "helloAsync", timeout = 10000)})
public class GreetingServiceImpl implements GreetingService {
    private static Logger logger = LoggerFactory.getLogger(GreetingServiceImpl.class);

    @DubboReference(group = "dg", version = "1.0.0")
    private EmbedService embedService;

    @Override
    public String hello() {
        return "Greetings";
    }

    /**
     * 异步操作不会堵塞，异步化耗时的操作并没有在 dubbo所在线程中继续占用资源，而是在新开辟的线程池或线程中占用资源。
     * 主要应用场景： 一些 IO 耗时的操作，比较影响客户体验和使用性能，某段业务逻辑开启异步执行后不太影响主线程的原有业务逻辑。
     */
    @Override
    public String helloAsync() {
        // 标识异步化模式开始
        AsyncContext asyncContext = RpcContext.startAsync();
        logger.info("sayHello start");

        // 异步操作在单独的线程 或 线程池中执行
        new Thread(() -> {
            // Use this method to switch RpcContext from a Dubbo thread to a new thread created by the user.
            asyncContext.signalContextSwitch();

            logger.info("async start");
            logger.info("Attachment from consumer: " + RpcContext.getServerAttachment().getAttachment("consumer-key1"));

            String embedCallResult = embedService.biz();
            // 模拟耗时的业务操作
            try {
                Thread.sleep(5000);
                logger.info("embedded call result: " + embedCallResult);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            asyncContext.write("response from provider: " + embedCallResult);
            logger.info("async end");
        }).start();

        logger.info("sayHello end");
        return "hello, async";
    }
}
