package task.redis.LeaderboardAPI.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import task.redis.LeaderboardAPI.service.ActivityService;
import task.redis.LeaderboardAPI.util.AnswerStatus;

@RestController
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    @PatchMapping("/activity")
    public ResponseEntity activity(@RequestHeader("username") String username, @RequestParam("number") Integer number) {
        if (number == 1) {
            activityService.userEvaluate(username, AnswerStatus.CORRECT);
        } else {
            activityService.userEvaluate(username, AnswerStatus.WRONG);
        }
        return ResponseEntity.ok().build();
    }
}
