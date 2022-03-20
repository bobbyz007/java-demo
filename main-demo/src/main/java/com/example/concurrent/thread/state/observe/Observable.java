package com.example.concurrent.thread.state.observe;

public interface Observable {
    enum Cycle {
        STARTED, RUNNING, DONE ,ERROR
    }

    Cycle getCycle();

    void start();

    void interrupt();
}
