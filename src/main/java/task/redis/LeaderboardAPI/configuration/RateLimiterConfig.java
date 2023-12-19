package task.redis.LeaderboardAPI.configuration;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.concurrent.ExecutionException;

@Slf4j
@Configuration
public class RateLimiterConfig {
    @Value("${rate.limit.key}")
    private String rateLimiterKey;

    @Bean
    @DependsOn({"redissonClient"})
    public RRateLimiter rateLimiter(RedissonClient redissonClient) {
        redissonClient.getKeys().delete(rateLimiterKey);
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(rateLimiterKey);
        return rateLimiter;
    }
}