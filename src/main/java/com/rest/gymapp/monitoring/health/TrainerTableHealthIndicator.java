package com.rest.gymapp.monitoring.health;

import com.rest.gymapp.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainerTableHealthIndicator implements HealthIndicator {

    private final TrainerRepository trainerRepository;

    @Override
    public Health health() {
        long count = trainerRepository.count();
        if (count > 0) {
            return Health.up().withDetail("trainerCount", count).build();
        } else {
            return Health.down().withDetail("trainerCount", 0).build();
        }
    }
}
