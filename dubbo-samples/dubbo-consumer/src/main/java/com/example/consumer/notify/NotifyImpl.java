package com.example.consumer.notify;

import com.example.client.notify.Notify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("notify")
public class NotifyImpl implements Notify {
    private static Logger logger = LoggerFactory.getLogger(NotifyImpl.class);
    public Map<Integer, Object> ret = new HashMap<>();

    @Override
    public void onReturn(String name, int id) {
        ret.put(id, name);
        logger.info("onReturn: " + name);
    }

    @Override
    public void onThrow(Throwable ex, int id) {
        ret.put(id, ex);
        logger.info("onThrow: " + ex);
    }

    @Override
    public void onInvoke(int id) {
        logger.info("onInvoke: " + id);
    }
}
