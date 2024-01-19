package com.example.provider.service.generic;

import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.service.GenericException;
import org.apache.dubbo.rpc.service.GenericService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * 仅服务提供者泛化， 此时interfaceName需要指定消费者使用的接口
 */
@DubboService(group = "dg", version = "1.0.0", interfaceName = "com.example.client.generic.impl.HelloService")
public class GenericImplServiceImpl implements GenericService {
    private static Logger logger = LoggerFactory.getLogger(GenericImplServiceImpl.class);

    @Override
    public Object $invoke(String method, String[] parameterTypes, Object[] args) throws GenericException {
        if (method.equals("sayHello")) {
            logger.info("executing sayHello.");
            return "sayHello: hello " + args[0];
        } else if (method.equals("sayHelloAsync")) {
            logger.info("executing sayHelloAsync.");
            return CompletableFuture.completedFuture("sayHelloAsync: hello " + args[0]);
        } else {
            throw new GenericException(new UnsupportedOperationException("method does not exist."));
        }
    }
}
