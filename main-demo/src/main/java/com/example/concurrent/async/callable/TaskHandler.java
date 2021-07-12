package com.example.concurrent.async.callable;

/**
 * 回调函数实现类
 */
public class TaskHandler implements TaskCallable<TaskResult>{
    @Override
    public TaskResult callable(TaskResult taskResult) {
        //TODO 拿到结果数据后进一步处理

        System.out.println(taskResult.toString());
        return taskResult;
    }
}
