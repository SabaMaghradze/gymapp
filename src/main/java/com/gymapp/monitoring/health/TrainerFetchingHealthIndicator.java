package com.gymapp.monitoring.health;

import com.gymapp.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TrainerFetchingHealthIndicator implements HealthIndicator {

    private final TrainerService trainerService;

    public Health health() {
        try {
            // simple check: can we fetch trainers?
            trainerService.getAllTrainers();
            return Health.up()
                    .withDetail("trainerService", "Available")
                    .build();
        } catch (Exception ex) {
            return Health.down()
                    .withDetail("trainerService", "Unavailable")
                    .withException(ex)
                    .build();
        }
    }
}
