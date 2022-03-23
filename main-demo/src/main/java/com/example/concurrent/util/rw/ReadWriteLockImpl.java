package com.example.concurrent.util.rw;

class ReadWriteLockImpl implements ReadWriteLock{
    private final Object MUTEX = new Object();

    private int writingWriters;

    private int waitingWriters;

    private int readingReaders;

    private boolean preferWriter;

    public ReadWriteLockImpl() {
        this(true);
    }

    public ReadWriteLockImpl(boolean preferWriter) {
        this.preferWriter = preferWriter;
    }

    @Override
    public Lock readLock() {
        return new ReadLock(this);
    }

    @Override
    public Lock writeLock() {
        return new WriteLock(this);
    }

    void incrementWritingWriters() {
        this.writingWriters++;
    }

    void incrementWaitingWriters() {
        this.waitingWriters++;
    }

    void incrementReadingReaders() {
        this.readingReaders++;
    }

    void decrementWritingWriters() {
        this.writingWriters--;
    }

    void decrementWaitingWriters() {
        this.waitingWriters--;
    }

    void decrementReadingReaders() {
        this.readingReaders--;
    }

    @Override
    public int getWaitingWriters() {
        return waitingWriters;
    }

    @Override
    public int getWritingWriters() {
        return writingWriters;
    }

    @Override
    public int getReadingReaders() {
        return readingReaders;
    }

    Object getMutex() {
        return MUTEX;
    }

    boolean getPreferWriter() {
        return preferWriter;
    }

    void changePreferWriter(boolean preferWriter) {
        this.preferWriter = preferWriter;
    }
}
