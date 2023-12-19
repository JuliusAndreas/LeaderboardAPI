package task.redis.LeaderboardAPI.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RFuture;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import task.redis.LeaderboardAPI.service.ActivityService;
import task.redis.LeaderboardAPI.util.AnswerStatus;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;
    private final RRateLimiter rateLimiter;
    @Value("${rate.limit.count}")
    private int count;
    @Value("${rate.limit.duration}")
    private int duration;

    @PostConstruct
    public void init() {
        RFuture<Boolean> booleanRFuture = rateLimiter.
                trySetRateAsync(RateType.PER_CLIENT, count, duration, RateIntervalUnit.SECONDS);
        try {
            if (!booleanRFuture.get()) throw new RuntimeException();
        } catch (Exception e) {
            log.error("Failed to initialize RateLimiter %s".formatted(e.getClass().toString()));
        }
        log.info("RateLimiter initialized with permits {} per {} seconds", rateLimiter.getConfig().getRate(),
                rateLimiter.getConfig().getRateInterval() / 1000);
    }

    @PatchMapping("/activity")
    public ResponseEntity activity(@RequestHeader("username") String username, @RequestParam("number") Integer number) {
        if (!rateLimiter.tryAcquire()) {
            return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
        }
        if (usernameIsInvalid(username)) {
            return new ResponseEntity("Failure", HttpStatus.BAD_REQUEST);
        }
        if (number == 1) {
            activityService.userEvaluate(username, AnswerStatus.CORRECT);
        } else {
            activityService.userEvaluate(username, AnswerStatus.WRONG);
        }
        return new ResponseEntity("Success", HttpStatus.OK);
    }

    public boolean usernameIsInvalid(String username) {
        return username == null || username.isEmpty() || username.isBlank();
    }
}
