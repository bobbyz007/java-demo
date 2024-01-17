package com.example.consumer;

import com.example.client.Util;
import com.example.consumer.controller.ConsumerController;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example"}) // 扫描其他包
@EnableDubbo(scanBasePackages = {"com.example.consumer"})
public class ConsumerApplication {
    public static void main(String[] args) {
        Util.setDubboCacheDir(new ApplicationHome(ConsumerApplication.class).getSource());

        SpringApplication.run(ConsumerApplication.class, args);
    }
}
