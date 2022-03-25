package com.example.concurrent.util.activeObject.general;

import com.example.concurrent.util.activeObject.ActiveFuture;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Future;

public class ActiveServiceFactory {
    private final static ActiveMessageQueue2 messageQueue = new ActiveMessageQueue2();

    public static <T> T active(T instance) {
        Object proxy = Proxy.newProxyInstance(instance.getClass().getClassLoader(),
                instance.getClass().getInterfaces(),
                new ActiveInvocationHandler<>(instance));

        return (T) proxy;
    }

    private static class ActiveInvocationHandler<T> implements InvocationHandler {
        private final T instance;

        public ActiveInvocationHandler(T instance) {
            this.instance = instance;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 标注了转换为 ActiveMessage
            // 注意：注解直接打开接口的方法上， 此处method不是对应实例的method，是接口的method
            if (method.isAnnotationPresent(ActiveMethod.class)) {
                checkMethod(method);

                ActiveMessage.Builder builder = new ActiveMessage.Builder();
                builder.useMethod(method).withParams(args).forService(instance);
                Object result = null;

                if (isReturnFutureType(method)) {
                    result = new ActiveFuture<>();
                    builder.returnFuture((ActiveFuture<Object>) result);
                }

                messageQueue.offer(builder.build());

                return result;

            } else {
                return method.invoke(instance, args);
            }
        }

        private void checkMethod(Method method) throws IllegalActiveMethodException {
            if (!isReturnFutureType(method) && !isReturnVoidType(method)) {
                throw new IllegalActiveMethodException("The method [" + method.getName() + "] return type must be Future or Void");
            }
        }

        private boolean isReturnFutureType(Method method) {
            return method.getReturnType().isAssignableFrom(Future.class);
        }

        private boolean isReturnVoidType(Method method) {
            return method.getReturnType().equals(Void.TYPE);
        }
    }

}
