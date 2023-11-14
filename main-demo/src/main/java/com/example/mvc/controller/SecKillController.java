package com.example.mvc.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 模拟解决秒杀活动中的超卖问题，基于lua脚本的原子化更新
 */
@RestController
@RequestMapping("seckill")
public class SecKillController {
    private static final String GOODS_CACHE = "seckill:goodsStock:";

    @Resource(name = "seckillRedisTemplate")
    private RedisTemplate<String, Serializable> redisTemplate;

    @Resource(name = "secKillLuaScript")
    private DefaultRedisScript<Number> redisScript;

    private String getCacheKey(String id) {
        return GOODS_CACHE.concat(id);
    }

    @RequestMapping("/prepare")
    public String prepare(@RequestParam(value = "id") String id, @RequestParam(value = "totalCount") int totalCount) {
        String key = getCacheKey(id);
        Map<String, Integer> goods = new HashMap<>();
        goods.put("totalCount", totalCount);
        goods.put("initStatus", 0);
        goods.put("seckillCount", 0);
        redisTemplate.opsForHash().putAll(key, goods);
        return "prepared: " + LocalDateTime.now();
    }

    @RequestMapping("/stock")
    public int secKill(@RequestParam(value = "id") String id, @RequestParam(value = "number")int number) {
        String key = getCacheKey(id);
        // 注意：number参数不需要转成字符串，否则传递到lua脚本中tonumber函数会返回null
        Object seckillCount = redisTemplate.execute(redisScript, Arrays.asList(key), number);
        return Integer.valueOf(seckillCount.toString());
    }
}
