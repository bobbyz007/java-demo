package com.example.consumer.cache;

import com.example.client.GreetingService;
import com.example.consumer.async.AsyncController;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("cache")
public class CacheController {
    private static Logger logger = LoggerFactory.getLogger(CacheController.class);

    @DubboReference(group = "dg", version = "1.0.0")
    private GreetingService greetingService;

    @GetMapping("/get")
    public String greeting() {
        logger.info("ready to run task");
        String fix = null;
        greetingService.findCache("0");
        for (int i = 0; i < 5; i++) {
            String result = greetingService.findCache("0");
            if (fix == null || fix.equals(result)) {
                logger.info("OK: " + result);
            } else {
                logger.error("ERROR: " + result);
            }
            fix = result;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // default cache.size is 1000 for LRU, should have cache expired if invoke more than 1001 times
        for (int n = 0; n < 5/*1001*/; n++) {
            String pre = null;
            greetingService.findCache(String.valueOf(n));
            for (int i = 0; i < 10; i++) {
                String result = greetingService.findCache(String.valueOf(n));
                if (pre != null && !pre.equals(result)) {
                    logger.error("ERROR: " + result);
                }
                pre = result;
            }
        }

        // verify if the first cache item is expired in LRU cache
        String result = greetingService.findCache("0");
        if (fix != null && !fix.equals(result)) {
            logger.info("OK: " + result);
        } else {
            logger.error("ERROR: " + result);
        }
        return "OK";
    }
}
