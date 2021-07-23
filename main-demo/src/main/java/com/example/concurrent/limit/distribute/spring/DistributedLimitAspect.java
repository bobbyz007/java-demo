package com.example.concurrent.limit.distribute.spring;

import com.example.concurrent.limit.distribute.spring.annotation.MyDistributedRateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

@Configuration
@Aspect
public class DistributedLimitAspect {
    private static final Logger logger = LoggerFactory.getLogger(DistributedLimitAspect.class);

    @Resource(name = "limitRedisTemplate")
    private RedisTemplate<String, Serializable> redisTemplate;

    @Autowired
    private DefaultRedisScript<Number> redisScript;

    /**
     * 一些常用的point cut配置：
     *
     * // 匹配指定包中的所有的方法
     * execution(* com.xys.service.*(..))
     *
     * // 匹配当前包中的指定类的所有方法
     * execution(* UserService.*(..))
     *
     * // 匹配指定包中的所有 public 方法
     * execution(public * com.xys.service.*(..))
     *
     * // 匹配指定包中的所有 public 方法, 并且返回值是 int 类型的方法
     * execution(public int com.xys.service.*(..))
     *
     * // 匹配指定包中的所有 public 方法, 并且第一个参数是 String, 返回值是 int 类型的方法
     * execution(public int com.xys.service.*(String name, ..))
     * 匹配类型签名
     *
     * // 匹配指定包中的所有的方法, 但不包括子包
     * within(com.xys.service.*)
     *
     * // 匹配指定包中的所有的方法, 包括子包
     * within(com.xys.service..*)
     *
     * // 匹配当前包中的指定类中的方法
     * within(UserService)
     *
     *
     * // 匹配一个接口的所有实现类中的实现的方法
     * within(UserDao+)
     * 匹配 Bean 名字
     *
     * // 匹配以指定名字结尾的 Bean 中的所有方法
     * bean(*Service)
     * 切点表达式组合
     *
     * // 匹配以 Service 或 ServiceImpl 结尾的 bean
     * bean(*Service || *ServiceImpl)
     *
     * // 匹配名字以 Service 开头, 并且在包 com.xys.service 中的 bean
     * bean(*Service) && within(com.xys.service.*)
     */
    @Around("execution(* com.example.controller ..*(..))")
    public Object intercept(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = method.getDeclaringClass();
        MyDistributedRateLimiter myDistributedRateLimiter = method.getAnnotation(MyDistributedRateLimiter.class);

        if (myDistributedRateLimiter != null) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String ipAddr = getIpAddr(request);

            StringBuffer stringBuffer = new StringBuffer();
            // 加入hash tag:{tag}处理，防止落入集群的不同slot而抛出异常：Lua script attempted to access a non local key in a cluster node
            stringBuffer.append(ipAddr).append("-")
                    .append(targetClass.getName()).append("- ")
                    .append(method.getName()).append("-{")
                    .append(myDistributedRateLimiter.key())
                    .append("}");

            List<String> keys = Collections.singletonList(stringBuffer.toString());
            // 执行 lua 脚本
            // 注意：整型参数不需要转成字符串，否则传递到lua脚本中tonumber函数会返回null
            Number number = redisTemplate.execute(redisScript, keys, myDistributedRateLimiter.count(), myDistributedRateLimiter.time());

            // 非0表示 正常访问
            if (number != null && number.intValue() != 0) {
                logger.info("限流时间段内正常访问");
                return joinPoint.proceed();
            }
        } else {
            // 没有限流注解，则继续执行
            return joinPoint.proceed();
        }

        //由于没有配置公共异常类，如果配置可替换
        throw new RuntimeException("已经达到限流阈值，访问被拦截");
    }

    private static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
                // = 15
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        return ipAddress;
    }
}
