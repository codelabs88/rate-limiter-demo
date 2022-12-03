package org.example;

import org.redisson.Redisson;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class CustomRedisson extends Redisson {

    protected CustomRedisson(Config config) {
        super(config);
    }

    public static RedissonClient create(Config config) {
        return new CustomRedisson(config);
    }

    @Override
    public RRateLimiter getRateLimiter(String name) {
        return new CustomRedissonRateLimiter(commandExecutor, name);
    }
}
