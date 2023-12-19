package task.redis.LeaderboardAPI.configuration;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "rate.limit")
public class RateLimiterConfig {

    @Value("${rate.limit.count}")
    private int count;
    @Value("${rate.limit.duration}")
    private int duration;

    @Bean
    @DependsOn({"redissonClient"})
    public RRateLimiter rateLimiter(RedissonClient redissonClient) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter("myRateLimiter");
        rateLimiter.trySetRate(RateType.PER_CLIENT, count, duration, RateIntervalUnit.SECONDS);
        log.info("RateLimiter initialized with permits {} per {} seconds", count, duration);
        return rateLimiter;
    }
}