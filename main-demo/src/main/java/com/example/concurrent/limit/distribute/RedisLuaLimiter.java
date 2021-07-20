package com.example.concurrent.limit.distribute;

import com.example.util.Constants;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.redisson.Redisson;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scripting.support.ResourceScriptSource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

/**
 * 演示redis 分布式限流：redis + lua
 */
public class RedisLuaLimiter {
    private static RedissonClient redissonClient = buildRedissonClient();
    private static RedissonClient buildRedissonClient() {
        Config config = new Config();
        config.useClusterServers().addNodeAddress(
                new String[]{"redis://139.9.40.183:6390", "redis://139.9.40.183:6391", "redis://139.9.40.183:6392",
                        "redis://139.9.40.183:6390", "redis://139.9.40.183:6391", "redis://139.9.40.183:6392"})
                .setTimeout(10000)
                .setConnectTimeout(20000)
                .setPassword(Constants.REDIS_CLUSTER_PWD);
        return Redisson.create(config);
    }

    public static boolean acquire() throws IOException {
        String luaScript = FileUtils.readFileToString(new File("D:\\workspace\\opensource\\java-demo\\main-demo\\src\\main\\java\\com\\example\\concurrent\\limit\\distribute\\limit.lua"), StandardCharsets.UTF_8);

        // 注意：要把limit.lua拷贝到build/classes对应的包路径下，否则找不到文件
        // 如：main-demo/build/classes/java/main/com/example/concurrent/limit/distribute/limit.lua
        // 或者修改构建脚本，自动拷贝
        /*ClassPathResource resource = new ClassPathResource("/com/example/concurrent/limit/distribute/limit.lua");
        ResourceScriptSource source = new ResourceScriptSource(resource);
        String luaScript = source.getScriptAsString();*/

        // 时间戳取秒数
        String key = "ip:" + System.currentTimeMillis() / 1000;
        String limit = "5";
        Long result = redissonClient.getScript(StringCodec.INSTANCE).eval(RScript.Mode.READ_WRITE, luaScript, RScript.ReturnType.INTEGER,
                Lists.newArrayList(key), new Object[]{limit});
        return result == 1;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final int THREAD_NUM = 10;
        CountDownLatch latch = new CountDownLatch(THREAD_NUM);
        for (int i = 0; i < THREAD_NUM; i++) {
            final int index = i;
            new Thread(() -> {
                try {
                    if (!acquire()) {
                        System.out.println("refused: " + index);
                    } else {
                        System.out.println("processed: " + index);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                latch.countDown();
            }).start();
        }

        latch.await();
        redissonClient.shutdown();
    }
}
