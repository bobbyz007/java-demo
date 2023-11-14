package com.example.resillience4j;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.functions.CheckedFunction;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class CircuitBreakerExample {
    public static void main(String[] args) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .slowCallRateThreshold(50)
                // 在open状态停留的时间
                .waitDurationInOpenState(Duration.ofMillis(10))
                .slowCallDurationThreshold(Duration.ofMillis(100))
                .permittedNumberOfCallsInHalfOpenState(3)
                .minimumNumberOfCalls(10)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(5)
                .recordException(e -> true)
                .recordExceptions(IOException.class, TimeoutException.class)
                .ignoreExceptions(NullPointerException.class)
                .build();
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        CircuitBreaker breaker = registry.circuitBreaker("name1");
        // breaker.getEventPublisher().onEvent(e -> System.out.println("event:" + e.getEventType()));

        /*CheckedFunction0<String> decoratedSupplier = CircuitBreaker.decorateCheckedSupplier(breaker,
                () -> {
                    //Files.readString(Path.of("d:\\test11111"));
                    Thread.sleep(150);
                    return "This can be any method which returns: 'Hello";
        });
        for (int i = 0; i < 10; i++) {
            try {
                System.out.println("result: " + decoratedSupplier.apply());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }*/
        CheckedFunction<Integer, String> decoratedSupplier1 = CircuitBreaker.decorateCheckedFunction(breaker,
                i -> {
                    if(i < 6) {
                        // mock slow calls
                        Thread.sleep(150);
                    } else {
                        // mock normal calls
                        Thread.sleep(60);
                    }

                    return "string " + i;
                });

        // 模拟并发访问
        for (int i = 0; i < 15; i++) {
            try {
                System.out.println("result: " + decoratedSupplier1.apply(i));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
