package com.example.concurrent.util.eda;

public interface DynamicRouter<E extends Message> {
    void registerChannel(Class<? extends E> messageType, Channel<? extends E> channel);

    void dispatch(E message);
}
