package com.example.concurrent.util.eventbus;

public interface EventExceptionHandler {
    void handle(Throwable cause, EventContext context);
}
