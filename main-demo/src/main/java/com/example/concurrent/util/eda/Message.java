package com.example.concurrent.util.eda;

public interface Message {
    Class<? extends Message> getType();
}
