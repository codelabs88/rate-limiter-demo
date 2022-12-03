package org.example;

import org.redisson.RedissonRateLimiter;
import org.redisson.api.RFuture;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.client.codec.LongCodec;
import org.redisson.client.protocol.RedisCommands;
import org.redisson.command.CommandAsyncExecutor;

import java.util.Collections;

public class CustomRedissonRateLimiter extends RedissonRateLimiter {

    public CustomRedissonRateLimiter(CommandAsyncExecutor commandExecutor, String name) {
        super(commandExecutor, name);
    }

    @Override
    public RFuture<Void> setRateAsync(RateType type, long rate, long rateInterval, RateIntervalUnit unit) {
        return commandExecutor.evalWriteAsync(getRawName(), LongCodec.INSTANCE, RedisCommands.EVAL_BOOLEAN,
                "redis.call('hset', KEYS[1], 'rate', ARGV[1]);"
                        + "redis.call('hset', KEYS[1], 'interval', ARGV[2]);"
                        + "redis.call('hset', KEYS[1], 'type', ARGV[3]);",
                Collections.singletonList(getRawName()), rate, unit.toMillis(rateInterval), type.ordinal());
    }

}
