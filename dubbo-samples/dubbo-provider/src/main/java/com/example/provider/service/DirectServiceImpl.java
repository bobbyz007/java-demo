package com.example.provider.service;

import com.example.client.direct.DirectService;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.rpc.RpcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

@DubboService(group = "dg", version = "1.0.0")
public class DirectServiceImpl implements DirectService {
    private static Logger logger = LoggerFactory.getLogger(DirectServiceImpl.class);

    @Override
    public String hello(String name) {
        logger.info("[{}] Hello {}, request from consumer: {}", new SimpleDateFormat("HH:mm:ss").format(new Date()),
                name, RpcContext.getServiceContext().getRemoteAddress());
        return "Hello " + name + ", response from provider: " + RpcContext.getServiceContext().getLocalAddress();
    }
}
