package com.example.concurrent.util.eda;

import java.util.HashMap;
import java.util.Map;

public class EventDispatcher implements DynamicRouter<Message>{
    private final Map<Class<? extends Message>, Channel> routeMap;

    public EventDispatcher() {
        this.routeMap = new HashMap<>();
    }

    @Override
    public void dispatch(Message message) {
        if (routeMap.containsKey(message.getType())) {
            routeMap.get(message.getType()).dispatch(message);
        } else {
            throw new MessageMatcherException("Can't match the channel for [" + message.getType() + "] type");
        }
    }

    @Override
    public void registerChannel(Class<? extends Message> messageType, Channel<? extends Message> channel) {
        routeMap.put(messageType, channel);
    }
}
