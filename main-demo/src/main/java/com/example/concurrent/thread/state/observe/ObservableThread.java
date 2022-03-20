package com.example.concurrent.thread.state.observe;

/**
 * Observable object
 * @param <T>
 */
public class ObservableThread<T> extends Thread implements Observable{
    private final LifecycleObserver<T> lifecycleObserver;

    private final Task<T> task;

    private Cycle cycle;

    public ObservableThread(Task<T> task) {
        this(task, new EmptyLifecycleObserver<>());

    }

    public ObservableThread(Task<T> task, LifecycleObserver<T> lifecycleObserver) {
        super();
        if (task == null) {
            throw new IllegalArgumentException("The task is required");
        }
        this.task = task;
        this.lifecycleObserver = lifecycleObserver;
    }

    @Override
    public void run() {
        update(Cycle.STARTED, null, null);
        try {
            update(Cycle.RUNNING, null, null);

            T result = task.call();
            update(Cycle.DONE, result, null);
        } catch (Exception e) {
            update(Cycle.ERROR, null, e);
        }
    }

    private void update(Cycle cycle, T result, Exception e) {
        this.cycle = cycle;
        if (lifecycleObserver == null) {
            return;
        }

        try {
            switch (cycle) {
                case STARTED:
                    lifecycleObserver.onStart(currentThread());
                    break;
                case RUNNING:
                    lifecycleObserver.onRunning(currentThread());
                    break;
                case DONE:
                    lifecycleObserver.onFinish(currentThread(), result);
                    break;
                case ERROR:
                    lifecycleObserver.onError(currentThread(), e);
                    break;
            }
        } catch (Exception ex) {
            // avoid observer exception to interfere with task running
            if (cycle == Cycle.ERROR) {
                throw ex;
            }
        }
    }

    @Override
    public Cycle getCycle() {
        return cycle;
    }
}
