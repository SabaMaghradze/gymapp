package com.gymapp.monitoring.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TrainerMetrics {

    private final Counter trainerCreatedCounter;

    public TrainerMetrics(MeterRegistry registry) {
        this.trainerCreatedCounter = Counter.builder("trainers_created")
                .description("Number of trainers created")
                .register(registry);
    }

    public void incrementTrainersCreated() {
        trainerCreatedCounter.increment();
    }

}
