package com.example.concurrent.seckill;

import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class SecKillConfig {
    /**
     * 执行的lua script脚本
     * @return
     */
    @Bean("secKillLuaScript")
    public DefaultRedisScript<String> redisLuaScript() {
        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/seckill.lua")));
        redisScript.setResultType(String.class);
        return redisScript;
    }

    /**
     * 初始化RedisTemplate
     *
     * @param redisConnectionFactory redisson connection factory
     * @return
     */
    @Bean("seckillRedisTemplate")
    public RedisTemplate<String, Serializable> limitRedisTemplate(RedissonConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Serializable> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
