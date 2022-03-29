package com.example.concurrent.util.eda.async;

import com.example.concurrent.util.eda.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AsyncEventDispatcher implements DynamicRouter<Event> {
    private final Map<Class<? extends Event>, AsyncChannel> routeMap;

    public AsyncEventDispatcher() {
        this.routeMap = new ConcurrentHashMap<>();
    }

    @Override
    public void registerChannel(Class<? extends Event> messageType, Channel<? extends Event> channel) {
        if (!(channel instanceof AsyncChannel)) {
            throw new IllegalArgumentException("The channel must be AsyncChannel Type.");
        }

        routeMap.put(messageType, (AsyncChannel) channel);
    }

    @Override
    public void dispatch(Event message) {
        if (routeMap.containsKey(message.getType())) {
            routeMap.get(message.getType()).dispatch(message);
        } else {
            throw new MessageMatcherException("Can't match the channel for [" + message.getType() + "] type");
        }
    }

    public void shutdown() {
        routeMap.values().forEach(AsyncChannel::stop);
    }
}
