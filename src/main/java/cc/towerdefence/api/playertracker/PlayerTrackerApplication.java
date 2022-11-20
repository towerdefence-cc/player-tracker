package cc.towerdefence.api.playertracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PlayerTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlayerTrackerApplication.class, args);
    }

}
