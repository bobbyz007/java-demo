package com.example.concurrent.util.eda;

public class Event implements Message{
    @Override
    public Class<? extends Message> getType() {
        return getClass();
    }
}
