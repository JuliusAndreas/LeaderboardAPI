package task.redis.LeaderboardAPI.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import task.redis.LeaderboardAPI.util.AnswerStatus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {
    private final RedissonClient redis;
    private RMap<String, Integer> leaderboard;
    public String leaderboardKey = "leaderboard";
    private final static String outputFilePath = "./leaderboard.txt";
    private boolean isWrittenToFile = false;

    @PostConstruct
    public void init() {
        LocalCachedMapOptions<Object, Object> options = LocalCachedMapOptions.defaults()
                .cacheSize(0)
                .evictionPolicy(LocalCachedMapOptions.EvictionPolicy.LRU)
                .maxIdle(10, TimeUnit.SECONDS)
                .timeToLive(60, TimeUnit.SECONDS);
        leaderboard = redis.getMapCache(leaderboardKey, new TypedJsonJacksonCodec(String.class));
        leaderboard.clear();
    }

    public void userEvaluate(String username, AnswerStatus answerStatus) {
        if (leaderboard.containsKey(username)) {
            leaderboard.addAndGet(username, answerStatus == AnswerStatus.CORRECT ? 1 : -1);
        } else {
            leaderboard.put(username, answerStatus == AnswerStatus.CORRECT ? 1 : -1);
        }
    }

    @Scheduled(initialDelay = 30, fixedDelay = 60, timeUnit = TimeUnit.SECONDS)
    public void releaseLock() {
        isWrittenToFile = false;
    }

    @Scheduled(fixedDelay = 60, timeUnit = TimeUnit.SECONDS)
    public void writeToFile() {
        redis.getKeys().delete("lock");
        RLock lock = redis.getFairLock("lock");
        lock.lock();
        if (isWrittenToFile) return;
        File file = new File(outputFilePath);
        try (BufferedWriter bf = new BufferedWriter(new FileWriter(file))) {
            for (RMap.Entry<String, Integer> entry : leaderboard.entrySet()) {
                bf.write(entry.getKey() + " : " + entry.getValue());
                bf.newLine();
            }
            log.info("successfully wrote {} records to the output file", leaderboard.size());
            bf.flush();
        } catch (IOException e) {
            log.error("Error encountered while trying to write to the file", e);
        }
        isWrittenToFile = true;
        lock.unlock();
    }

}
