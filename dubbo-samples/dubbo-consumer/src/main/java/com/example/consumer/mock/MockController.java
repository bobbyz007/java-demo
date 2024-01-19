package com.example.consumer.mock;

import com.example.client.mock.DemoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("mock")
public class MockController {
    private static Logger logger = LoggerFactory.getLogger(MockController.class);

    /**
     * 指定mock参数
     * 如果mock参数不是以 return 或 throw 开头， 则默认寻找服务名+Mock的类作为Mock实现
     * 参考 {@link org.apache.dubbo.rpc.support.MockInvoker MockInvoker} 实现
     */
    @DubboReference(group = "dg", version = "1.0.0", mock = "true", retries = 0)
    private DemoService demoService;

    @GetMapping("/get")
    public String call() {
        // 模拟调用超时，调用Mock实现
        String result = demoService.sayHello("world");
        logger.info("result: " + result);
        return result;
    }
}
