package com.example.mvc.controller;

import org.redisson.RedissonMultiLock;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
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

import java.time.LocalDateTime;

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

    @GetMapping("/lock/reentrant")
    public String testReentrantLock() {
        RLock lock = redisson.getLock("anylock");
        try {
            lock.lock();
            System.out.println("Do business under reentrant lock");
            // mock business，此时可以在redis集群中共通过以下命令查询到
            // hgetall anylock
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Do business successfully");
            lock.unlock();
        }
        return "locked: " + LocalDateTime.now();
    }

    @GetMapping("/lock/reentrant2")
    public String testReentrantLock2() {
        RLock lock = redisson.getLock("anylock");
        try {
            lock.lock();
            System.out.println("Do another business under reentrant lock");
            // mock business，此时可以在redis集群中共通过以下命令查询到
            // hgetall anylock
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Do another business successfully");
            lock.unlock();
        }
        return "locked: " + LocalDateTime.now();
    }

    /**
     * 联锁：需要同时锁定多条记录
     * @return
     */
    @GetMapping("/multilock")
    public String testMultiLock() {
        RLock lock1 = redisson.getLock("lock1");
        RLock lock2 = redisson.getLock("lock2");
        RLock lock3 = redisson.getLock("lock3");
        RedissonMultiLock lock = new RedissonMultiLock(lock1, lock2, lock3);
        try {
            lock.lock();
            System.out.println("Do business under multi lock");
            // mock business，此时可以在redis集群中共通过以下命令查询到
            // hgetall anylock
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Do business successfully");
            lock.unlock();
        }
        return "multi locked: " + LocalDateTime.now();
    }
}
