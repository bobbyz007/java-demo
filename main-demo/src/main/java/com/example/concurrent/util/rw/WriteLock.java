package com.example.concurrent.util.rw;

public class WriteLock implements Lock{
    private final ReadWriteLockImpl readWriteLock;

    WriteLock(ReadWriteLockImpl readWriteLock) {
        this.readWriteLock = readWriteLock;
    }

    @Override
    public void lock() throws InterruptedException {
        synchronized (readWriteLock.getMutex()) {
            try {
                readWriteLock.incrementWaitingWriters();

                while (readWriteLock.getReadingReaders() > 0 || readWriteLock.getWritingWriters() > 0) {
                    readWriteLock.getMutex().wait();
                }
            } finally {
                // 成功获取到了锁
                readWriteLock.decrementWaitingWriters();
            }

            readWriteLock.incrementWritingWriters();
        }
    }

    @Override
    public void unlock() {
        synchronized (readWriteLock.getMutex()) {
            readWriteLock.decrementWritingWriters();

            // 修改为false，使得读线程获得锁的可能性。（在Reader中更大可能不会挂起）
            readWriteLock.changePreferWriter(false);

            readWriteLock.getMutex().notifyAll();
        }
    }
}
