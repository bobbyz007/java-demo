package com.example.concurrent.util.eda;

public interface Channel<E extends Message> {
    void dispatch(E message);
}