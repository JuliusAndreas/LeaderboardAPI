package task.redis.LeaderboardAPI.configuration;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Data
@Configuration
@RequiredArgsConstructor
public class BeanPool {
    private final RedisConfig redisConfig;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(redisConfig.getHost().concat(redisConfig.getPort())).setTimeout(10000);
        return Redisson.create(config);
    }
}
