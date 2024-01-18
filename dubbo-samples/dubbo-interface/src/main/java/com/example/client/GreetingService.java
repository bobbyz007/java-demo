package com.example.client;

import java.util.concurrent.CompletableFuture;

public interface GreetingService {
    String hello();

    String helloAsync();

    CompletableFuture<String> helloFuture();

    String findCache(String id);
}
