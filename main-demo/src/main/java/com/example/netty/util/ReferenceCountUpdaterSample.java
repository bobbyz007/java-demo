package com.example.netty.util;

import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ReferenceCountUpdater;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * 演示ReferenceCountUpdater用法，实际上包装了AtomicIntegerFieldUpdater，增加了异常判断，比如超过int最大值则抛出异常。
 */
public class ReferenceCountUpdaterSample implements ReferenceCounted {
    public static void main(String[] args) throws InterruptedException {
        ReferenceCountUpdaterSample main = new ReferenceCountUpdaterSample();
        // Integer.MAX_VALUE=2147_483_647
        // 3个线程模拟累加次数超过阈值(3 * 1000_000_000L > Integer.MAX_VALUE)会抛出异常。
        final long count = 1000_000_000L;
        final short threadCount = 3;

        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; ++i) {
            new Thread(() -> {
                for (int j = 0; j < count; ++j) {
                    main.retain();
                }
                latch.countDown();
            }).start();
        }

        // wait all threads terminated
        latch.await();

        System.out.println("refCnt: " + main.refCnt());
    }

    private static final long REFCNT_FIELD_OFFSET =
            ReferenceCountUpdater.getUnsafeOffset(ReferenceCountUpdaterSample.class, "refCnt");
    private static final AtomicIntegerFieldUpdater<ReferenceCountUpdaterSample> AIF_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(ReferenceCountUpdaterSample.class, "refCnt");

    private static final ReferenceCountUpdater<ReferenceCountUpdaterSample> updater = new ReferenceCountUpdater<>() {
        @Override
        protected AtomicIntegerFieldUpdater<ReferenceCountUpdaterSample> updater() {
            return AIF_UPDATER;
        }
        @Override
        protected long unsafeOffset() {
            return REFCNT_FIELD_OFFSET;
        }
    };

    private volatile int refCnt = updater.initialValue();

    @Override
    public int refCnt() {
        return updater.refCnt(this);
    }

    @Override
    public ReferenceCounted retain() {
        return updater.retain(this);
    }

    @Override
    public ReferenceCounted retain(int increment) {
        return updater.retain(this, increment);
    }

    @Override
    public ReferenceCounted touch() {
        return this;
    }

    @Override
    public ReferenceCounted touch(Object hint) {
        return this;
    }

    @Override
    public boolean release() {
        return updater.release(this);
    }

    @Override
    public boolean release(int decrement) {
        return updater.release(this, decrement);
    }
}
