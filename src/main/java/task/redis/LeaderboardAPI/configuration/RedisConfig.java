package task.redis.LeaderboardAPI.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "redis.single")
public class RedisConfig {
    private String host;
    private String port;
}
