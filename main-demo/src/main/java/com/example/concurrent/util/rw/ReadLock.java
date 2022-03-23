package com.example.concurrent.util.rw;

public class ReadLock implements Lock{
    private final ReadWriteLockImpl readWriteLock;

    ReadLock(ReadWriteLockImpl readWriteLock) {
        this.readWriteLock = readWriteLock;
    }

    @Override
    public void lock() throws InterruptedException {
        synchronized (readWriteLock.getMutex()) {
            while (readWriteLock.getWritingWriters() > 0 ||
                    (readWriteLock.getPreferWriter() && readWriteLock.getWaitingWriters() > 0)) {
                // 无法获得读锁，挂起线程
                readWriteLock.getMutex().wait();
            }

            //成功获得读锁
            readWriteLock.incrementReadingReaders();
        }
    }

    @Override
    public void unlock() {
        synchronized (readWriteLock.getMutex()) {
            readWriteLock.decrementReadingReaders();

            // 使得writer线程获得更多机会
            readWriteLock.changePreferWriter(true);
            // 唤醒其他堵塞线程，其实也就是写线程
            readWriteLock.getMutex().notifyAll();
        }
    }
}
