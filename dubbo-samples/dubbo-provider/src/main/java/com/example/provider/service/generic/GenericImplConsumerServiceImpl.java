package com.example.provider.service.generic;

import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.service.GenericException;
import org.apache.dubbo.rpc.service.GenericService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * 服务提供者和服务消费者都是基于泛化，其中interfaceName可以指定任何值，只需保证消费者和提供者使用同样的值
 */
@DubboService(group = "dg", version = "1.0.0", interfaceName = "any.name.xxx")
public class GenericImplConsumerServiceImpl implements GenericService {
    private static Logger logger = LoggerFactory.getLogger(GenericImplConsumerServiceImpl.class);

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
