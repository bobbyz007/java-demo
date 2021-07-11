package com.example.controller;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
public class RedissonController {
    @Autowired
    private RedissonClient redisson;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/get/{key}")
    public String get(@PathVariable("key") String key) {
        RBucket<String> rBucket = redisson.getBucket(key, StringCodec.INSTANCE);
        ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
        return String.format("string redis template: %s, redisson client: %s", opsForValue.get(key), rBucket.get());
    }
}
