package com.example.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RedissonConfig {
    @Autowired
    private RedisConfigProperties redisConfigProperties;

    @Bean
    public RedissonClient redissonClient() {
        List<String> clusterNodes = new ArrayList<>();
        for (int i = 0; i < redisConfigProperties.getCluster().getNodes().size(); i++) {
            clusterNodes.add("redis://" + redisConfigProperties.getCluster().getNodes().get(i));
        }

        Config config = new Config();
        ClusterServersConfig clusterServersConfig = config.useClusterServers()
                .addNodeAddress(clusterNodes.toArray(new String[clusterNodes.size()]));
        clusterServersConfig.setPassword(redisConfigProperties.getPassword());
        clusterServersConfig.setTimeout(redisConfigProperties.getTimeout());
        clusterServersConfig.setConnectTimeout(redisConfigProperties.getConnectTimeout());
        return Redisson.create(config);
    }

}
