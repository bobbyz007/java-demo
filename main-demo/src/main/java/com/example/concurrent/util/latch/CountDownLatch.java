package com.example.concurrent.util.latch;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CountDownLatch extends Latch{
    public CountDownLatch(int limit) {
        super(limit);
    }

    @Override
    public void await() throws InterruptedException {
        synchronized (this) {
            while (limit > 0) {
                wait();
            }
        }
    }

    @Override
    public void await(TimeUnit unit, long time) throws InterruptedException, TimeoutException {
        if (time <= 0) {
            throw new IllegalArgumentException("The time is invalid");
        }
        long remainingNanos = unit.toNanos(time);
        long endNanos = System.nanoTime() + remainingNanos;

        synchronized (this) {
            while (limit > 0) {
                long remainingMillis = TimeUnit.NANOSECONDS.toMillis(remainingNanos);
                if (remainingMillis <= 0) {
                    throw new TimeoutException("Wait time out");
                }

                wait(remainingMillis);
                // wait之后更新
                remainingNanos = endNanos - System.nanoTime();
            }
        }
    }

    @Override
    public void countDown() {
        synchronized (this) {
            if (limit <= 0) {
                throw new IllegalStateException("All tasks already arrived");
            }

            limit--;
            this.notifyAll();
        }
    }

    @Override
    public int getUnarrived() {
        return limit;
    }
}
