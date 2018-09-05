package com.aaxis.redis.redislock.util;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@Data
public class RedissonConnector {

    RedissonClient redisson;

    @Value("#{'${redis.connection.info}'.split(',')}")
    private List<String> redisConnectionInfo;

    @PostConstruct
    public void init(){
        Config config = new Config();
        config.useClusterServers()
                .setScanInterval(2000); // cluster state scan interval in milliseconds
                // use "rediss://" for SSL connection

        for (String redisInfo : redisConnectionInfo) {
            config.useClusterServers().addNodeAddress(redisInfo);
        }
        redisson = Redisson.create(config);
    }

    public RedissonClient getClient(){
        return redisson;
    }

}