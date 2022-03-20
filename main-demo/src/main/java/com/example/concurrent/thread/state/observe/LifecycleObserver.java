package com.example.concurrent.thread.state.observe;

/**
 *
 * @param <T> task result
 */
public interface LifecycleObserver<T> {
    void onStart(Thread thread);

    void onRunning(Thread thread);

    void onFinish(Thread thread, T result);

    void onError(Thread thread, Exception e);
}

class EmptyLifecycleObserver<T> implements LifecycleObserver<T> {
    @Override
    public void onStart(Thread thread) {
    }

    @Override
    public void onRunning(Thread thread) {
    }

    @Override
    public void onFinish(Thread thread, T result) {
    }

    @Override
    public void onError(Thread thread, Exception e) {
    }
}
