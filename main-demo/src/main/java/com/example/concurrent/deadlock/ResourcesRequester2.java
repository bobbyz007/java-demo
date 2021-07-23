package com.example.concurrent.deadlock;

import java.util.ArrayList;
import java.util.List;

/**
 * 一次性申请所有资源，以及释放所有资源：基于线程的通知等待机制
 */
public class ResourcesRequester2 {
    //存放申请资源的集合
    private List<Object> resources = new ArrayList<Object>();

    private ResourcesRequester2(){}
    public static ResourcesRequester2 getInstance() {
        // 基于枚举方式获取单例，保证了线程安全
        return ResourcesRequesterEnum.INSTANCE.getInstance();
    }

    // 单例的线程安全也可以用enum来实现，因为enum不能继承且只能被实例化一次，天生的单例模式
    private enum ResourcesRequesterEnum {
        INSTANCE;

        private ResourcesRequester2 instance;
        ResourcesRequesterEnum() {
            this.instance = new ResourcesRequester2();
        }

        public ResourcesRequester2 getInstance() {
            return instance;
        }
    }

    //一次申请所有的资源
    public synchronized void applyResources(Object source, Object target){
        // while检查是否满足条件
        while (resources.contains(source) || resources.contains(target)){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        resources.add(source);
        resources.add(target);
    }

    //释放资源
    public synchronized void releaseResources(Object source, Object target){
        resources.remove(source);
        resources.remove(target);
        notifyAll();
    }
}
