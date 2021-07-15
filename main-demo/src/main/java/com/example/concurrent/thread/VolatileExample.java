package com.example.concurrent.thread;

public class VolatileExample {
    private volatile int a = 0;

    private int write(int b) {
        this.a = b;
        return b * 2;
    }
}
