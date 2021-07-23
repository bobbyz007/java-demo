package com.example.concurrent.limit.app.spring;

import com.example.concurrent.limit.app.spring.annotation.MySingleRateLimiter;
import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

@Configuration
@Aspect
public class SingleLimitAspect {
    private static final Logger logger = LoggerFactory.getLogger(SingleLimitAspect.class);

    private RateLimiter rateLimiter = RateLimiter.create(2);

    @Around("execution(* com.example.controller ..*(..))")
    public Object intercept(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        MySingleRateLimiter mySingleRateLimiter = signature.getMethod().getDeclaredAnnotation(MySingleRateLimiter.class);

        if (mySingleRateLimiter != null) {
            //获取注解上的参数
            //获取配置的速率
            double rate = mySingleRateLimiter.rate();
            //获取客户端等待令牌的时间
            long timeout = mySingleRateLimiter.timeout();
            //设置限流速率
            rateLimiter.setRate(rate);

            //判断客户端获取令牌是否超时
            boolean tryAcquire = rateLimiter.tryAcquire(timeout, TimeUnit.MILLISECONDS);
            if(!tryAcquire){
                //服务降级
                fullback();
                return null;
            }

            return joinPoint.proceed();
        } else {
            // 没有限流注解，则继续执行
            return joinPoint.proceed();
        }
    }

    /**
     * 降级处理
    */
    private void fullback() {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.println("出错了，重试一次试试？");
            writer.flush();;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(writer != null){
                writer.close();
            }
        }
    }
}
