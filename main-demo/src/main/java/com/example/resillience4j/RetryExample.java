package com.example.resillience4j;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;

import java.time.Duration;

public class RetryExample {
    public static void main(String[] args) {
        RetryConfig config = RetryConfig.<String>custom()
                .maxAttempts(2)
                .waitDuration(Duration.ofMillis(100))
                .retryOnResult(result -> result.equals("fail"))
                .build();

        RetryRegistry registry = RetryRegistry.of(config);
        Retry retry = registry.retry("name1");
        try {
            retry.executeCallable(() -> {
                System.out.println("executing...");
                return "fail";
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
