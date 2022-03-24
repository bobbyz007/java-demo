package com.example.concurrent.util.latch;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 实现Latch设计模式
 */
public abstract class Latch {
    protected int limit;

    public Latch(int limit) {
        this.limit = limit;
    }

    // 允许被中断
    public abstract void await() throws InterruptedException;
    public abstract void await(TimeUnit unit, long time) throws InterruptedException, TimeoutException;

    public abstract void countDown();

    public abstract int getUnarrived();

}
