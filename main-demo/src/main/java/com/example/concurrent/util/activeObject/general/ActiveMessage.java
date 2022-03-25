package com.example.concurrent.util.activeObject.general;

import com.example.concurrent.util.activeObject.ActiveFuture;

import java.lang.reflect.Method;
import java.util.concurrent.Future;

class ActiveMessage {
    // 接口方法的参数
    private final Object[] params;

    private final Method method;

    private final ActiveFuture<Object> future;

    private final Object service;

    private ActiveMessage(Builder builder) {
        this.params = builder.params;
        this.method = builder.method;
        this.future = builder.future;
        this.service = builder.service;
    }

    public void execute() {
        try {
            Object result = method.invoke(service, params);
            if (future != null) {
                Future<?> realFuture = (Future<?>) result;
                future.set(realFuture.get());
            }
        } catch (Exception e) {
            // 发生异常时，设置为null
            if (future != null) {
                future.set(null);
            }
        }
    }

    static class Builder {
        // 接口方法的参数
        private Object[] params;

        private Method method;

        private ActiveFuture<Object> future;

        private Object service;

        public Builder useMethod(Method method) {
            this.method = method;
            return this;
        }

        public Builder returnFuture(ActiveFuture<Object> future) {
            this.future = future;
            return this;
        }

        public Builder withParams(Object[] params) {
            this.params = params;
            return this;
        }

        public Builder forService(Object service) {
            this.service = service;
            return this;
        }

        public ActiveMessage build() {
            return new ActiveMessage(this);
        }
    }

}
