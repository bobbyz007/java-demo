package com.example.consumer.notify;

import com.example.client.notify.NotifyService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Consumer端调用远程服务后，如果希望在方法执行后，正常返回后，出现异常后做一些额外的处理逻辑，
 * 可以分别通过配置方法的oninvoke、onreturn、onthrow属性来执行对应的逻辑处理
 */
@RestController
@RequestMapping("notify")
public class NotifyController {
    private static Logger logger = LoggerFactory.getLogger(NotifyController.class);

    // 配置方法执行后的一些额外的处理逻辑，表示notify的bean的onReturn方法
    @DubboReference(group = "dg", version = "1.0.0",
            methods = @Method(name = "sayHello", onreturn = "notify.onReturn", onthrow = "notify.onThrow"))
    private NotifyService demoService;

    @Autowired
    private NotifyImpl notify;

    /**
     * 模拟 id> 10 抛出异常
     */
    @GetMapping("/get/{id}")
    public String call(@PathVariable int id) throws InterruptedException {
        try {
            demoService.sayHello(id);
        } catch (Exception e) {
            // IGNORE
        }

        // 等待通知执行完成
        for (int i = 0; i < 10; i++) {
            if (!notify.ret.containsKey(id)) {
                Thread.sleep(200);
            } else {
                break;
            }
        }

        Object obj = notify.ret.get(id);
        String result;
        if (obj instanceof Throwable) {
            result = ((Throwable) obj).getMessage();
        } else {
            result = obj.toString();
        }
        logger.info("result: {}", result);
        return result;
    }
}
