package org.example;

import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.atomic.AtomicInteger;

public class Application {

    public static void main(String[] args) {

        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379");
        RedissonClient redisson = CustomRedisson.create(config);

        RRateLimiter limiter = redisson.getRateLimiter("sms::rate::limiter::us_112358");

        // 1 permits per 5 seconds
        limiter.setRate(RateType.OVERALL, 1, 5, RateIntervalUnit.SECONDS);
        AtomicInteger succeedCounter = new AtomicInteger(0);

        new Thread(() -> {
            while (true) {
                if (limiter.tryAcquire()) {
                    succeedCounter.incrementAndGet();
                }

                System.out.println(succeedCounter.get());

                sleep();
            }
        }).start();

    }

    static void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
