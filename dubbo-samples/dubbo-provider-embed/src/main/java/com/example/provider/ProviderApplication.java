package com.example.provider;

import com.example.client.Util;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example"}) // 扫描其他包
@EnableDubbo(scanBasePackages = {"com.example.provider"})
public class ProviderApplication {
    public static void main(String[] args) {
        Util.setDubboCacheDir(new ApplicationHome(ProviderApplication.class).getSource());

        SpringApplication.run(ProviderApplication.class, args);
    }
}
