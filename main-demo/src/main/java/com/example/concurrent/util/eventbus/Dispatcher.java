package com.example.concurrent.util.eventbus;

import com.google.common.util.concurrent.MoreExecutors;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Dispatcher {
    private final Executor executor;

    private final EventExceptionHandler eventExceptionHandler;

    // MoreExecutors.newSequentialExecutor(Executors.newCachedThreadPool());
    public static final Executor SEQ_EXECUTOR = SeqExecutorService.INSTANCE;

    public static final Executor POOL_EXECUTOR = Executors.newCachedThreadPool();

    private Dispatcher(Executor executor, EventExceptionHandler eventExceptionHandler) {
        this.executor = executor;
        this.eventExceptionHandler = eventExceptionHandler;
    }

    public void dispatch(Bus bus, Registry registry, Object event, String topic) {
        ConcurrentLinkedQueue<Subscriber> subscribers = registry.scanSubscriber(topic);

        if (subscribers == null) {
            if (eventExceptionHandler != null) {
                eventExceptionHandler.handle(new IllegalArgumentException("The topic " + topic + " not bound yet"),
                        new BaseEventContext(bus.getBusName(), null, event));
                return;
            }
        }

        subscribers.stream()
                .filter(s -> !s.isDisabled())
                .filter(s -> {
                    Method subscribeMethod = s.getMethod();
                    Class<?> paramClass = subscribeMethod.getParameterTypes()[0];
                    return paramClass.isAssignableFrom(event.getClass());
                })
                .forEach(s -> realInvokeSubscribe(s, event, bus));
    }

    private void realInvokeSubscribe(Subscriber subscriber, Object event, Bus bus) {
        Method subscribeMethod = subscriber.getMethod();
        Object subscribeObject = subscriber.getObject();

        executor.execute(() -> {
            try {
                subscribeMethod.invoke(subscribeObject, event);
            } catch (Exception e) {
                if (eventExceptionHandler != null) {
                    eventExceptionHandler.handle(e, new BaseEventContext(bus.getBusName(), subscriber, event));
                }
            }
        });
    }

    public void close() {
        if (executor instanceof ExecutorService) {
            ((ExecutorService) executor).shutdown();
        }
    }

    static Dispatcher newDispatcher(EventExceptionHandler eventExceptionHandler, Executor executor) {
        return new Dispatcher(executor, eventExceptionHandler);
    }

    static Dispatcher newSeqDispatcher(EventExceptionHandler eventExceptionHandler) {
        return new Dispatcher(SEQ_EXECUTOR, eventExceptionHandler);
    }

    static Dispatcher newPooledDispatcher(EventExceptionHandler eventExceptionHandler) {
        return new Dispatcher(POOL_EXECUTOR, eventExceptionHandler);
    }

    private static class SeqExecutorService implements Executor {
        private final static SeqExecutorService INSTANCE = new SeqExecutorService();
        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }


    private static class BaseEventContext implements EventContext {
        private final String busName;
        private final Subscriber subscriber;
        private final Object event;

        private BaseEventContext(String busName, Subscriber subscriber, Object event) {
            this.busName = busName;
            this.subscriber = subscriber;
            this.event = event;
        }

        @Override
        public String getSource() {
            return busName;
        }

        @Override
        public Object getSubscriber() {
            return subscriber != null ? subscriber.getObject() : null;
        }

        @Override
        public Method getSubscribe() {
            return subscriber != null ? subscriber.getMethod() : null;
        }

        @Override
        public Object getEvent() {
            return event;
        }
    }
}
