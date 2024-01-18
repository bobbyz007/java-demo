package com.example.provider.service;

import com.example.client.callback.CallbackListener;
import com.example.client.callback.CallbackService;
import org.apache.dubbo.config.annotation.Argument;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 当调用的是Callback方法时，Consumer端发送请求的同时暴露一个回调参数的服务，
 * 这样Provider返回结果的方式就变成了调用Consumer暴露的这个服务，也就是返回结果时Provider变成了Consumer。
 * 总结：基于长连接生成反向代理，这样就可以从服务器端调用客户端逻辑。
 *
 * 配置call参数， 注意注解参数callbacks 是指限制同一个连接的回调实例(在调用端的CallbackListener的实例个数）个数的。
 * 而并不是限制consumer的回调次数。
 */
@DubboService(group = "dg", version = "1.0.0", token = "true", connections = 1, callbacks = 1000,
        methods = {@Method(name = "biz", arguments = @Argument(index = 1, callback = true))})
public class CallbackServiceImpl implements CallbackService {
    private final Map<String, CallbackListener> listeners = new ConcurrentHashMap<String, CallbackListener>();

    public CallbackServiceImpl() {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    for (Map.Entry<String, CallbackListener> entry : listeners.entrySet()) {
                        try {
                            entry.getValue().changed(getMsg(entry.getKey()));
                        } catch (Throwable t1) {
                            listeners.remove(entry.getKey());
                        }
                    }
                    Thread.sleep(30000); // timely trigger change event
                } catch (Throwable t1) {
                    t1.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void biz(String key, CallbackListener listener) {
        listeners.put(key, listener);
        listener.changed(getMsg(key));
    }

    private String getMsg(String key) {
        return "Changed: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
