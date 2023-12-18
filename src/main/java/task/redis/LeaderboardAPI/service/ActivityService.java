package task.redis.LeaderboardAPI.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.stereotype.Service;
import task.redis.LeaderboardAPI.util.AnswerStatus;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final RedissonClient redis;
    private RMap<String, Integer> leaderboard;
    public String leaderboardKey = "leaderboard";

    @PostConstruct
    public void init() {
        LocalCachedMapOptions options = LocalCachedMapOptions.defaults()
                .cacheSize(0)
                .evictionPolicy(LocalCachedMapOptions.EvictionPolicy.LRU)
                .maxIdle(10, TimeUnit.SECONDS)
                .timeToLive(60, TimeUnit.SECONDS);
        leaderboard = redis.getLocalCachedMap(leaderboardKey, new TypedJsonJacksonCodec(String.class) ,options);
        leaderboard.clear();
    }

    public void userEvaluate(String username, AnswerStatus answerStatus) {
        if (leaderboard.containsKey(username)) {
            leaderboard.addAndGet(username, answerStatus == AnswerStatus.CORRECT ? 1 : -1);
        } else {
            leaderboard.put(username, answerStatus == AnswerStatus.CORRECT ? 1 : -1);
        }
    }
}
