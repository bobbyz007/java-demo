package com.example.provider.service.generic;

import com.example.client.generic.consumer.GenericType;
import com.example.client.generic.consumer.HelloService;
import com.example.client.generic.consumer.Person;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.concurrent.CompletableFuture;

/**
 * 仅服务消费者泛化
 */
@DubboService(group = "dg", version = "1.0.0")
public class GenericConsumerServiceImpl implements HelloService {

    @Override
    public String sayHello(String name) {
        return "sayHello: " + name;
    }

    @Override
    public CompletableFuture<String> sayHelloAsync(String name) {
        CompletableFuture<String> future = new CompletableFuture<>();
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            future.complete("sayHelloAsync: " + name);
        }).start();

        return future;
    }

    @Override
    public CompletableFuture<Person> sayHelloAsyncComplex(String name) {
        Person person = new Person(1, "sayHelloAsyncComplex: " + name);
        CompletableFuture<Person> future = new CompletableFuture<>();
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            future.complete(person);
        }).start();

        return future;
    }

    @Override
    public CompletableFuture<GenericType<Person>> sayHelloAsyncGenericComplex(String name) {
        Person person = new Person(1, "sayHelloAsyncGenericComplex: " + name);
        GenericType<Person> genericType = new GenericType<>(person);
        CompletableFuture<GenericType<Person>> future = new CompletableFuture<>();
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            future.complete(genericType);
        }).start();

        return future;
    }
}
