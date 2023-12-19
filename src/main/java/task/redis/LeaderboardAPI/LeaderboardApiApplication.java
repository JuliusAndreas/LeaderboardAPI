package task.redis.LeaderboardAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LeaderboardApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeaderboardApiApplication.class, args);
	}

}
