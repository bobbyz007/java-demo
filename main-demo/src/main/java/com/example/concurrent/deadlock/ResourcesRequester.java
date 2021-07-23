package com.example.concurrent.deadlock;

import java.util.ArrayList;
import java.util.List;

/**
 * 一次性申请所有资源，以及释放所有资源
 */
public class ResourcesRequester {
    //存放申请资源的集合
    private List<Object> resources = new ArrayList<Object>();

    // 推荐使用单例的holder方式：线程安全，因为借助于类加载 的初始化阶段<clinit>() 方法是同步方法。
    private static class ResourcesRequesterHolder {
        private static ResourcesRequester instance = new ResourcesRequester();
    }

    private ResourcesRequester(){}
    public static ResourcesRequester getInstance() {
        // ResourcesRequesterHolder加载的时候是同步的，保证了线程安全
        return ResourcesRequesterHolder.instance;
    }

    //一次申请所有的资源
    public synchronized boolean applyResources(Object source, Object target){
        if(resources.contains(source) || resources.contains(target)){
            return false;
        }
        resources.add(source);
        resources.add(target);
        return true;
    }

    //释放资源
    public synchronized void releaseResources(Object source, Object target){
        resources.remove(source);
        resources.remove(target);
    }
}
