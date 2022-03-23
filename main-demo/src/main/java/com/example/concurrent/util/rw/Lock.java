package com.example.concurrent.util.rw;

public interface Lock {
    void lock() throws InterruptedException;

    void unlock();
}
