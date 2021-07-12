package com.example.concurrent.async.callable;

/**
 * 回调接口：用于处理线程返回的结果
 */
public interface TaskCallable<T> {
    T callable(T t);
}
