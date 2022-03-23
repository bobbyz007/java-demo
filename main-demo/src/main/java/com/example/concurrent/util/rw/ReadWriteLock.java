package com.example.concurrent.util.rw;

public interface ReadWriteLock {
    Lock readLock();

    Lock writeLock();

    int getWritingWriters();

    int getWaitingWriters();

    int getReadingReaders();

    static ReadWriteLock readWriteLock() {
        return new ReadWriteLockImpl();
    }

    static ReadWriteLock readWriteLock(boolean preferWriter) {
        return new ReadWriteLockImpl(preferWriter);
    }
}
