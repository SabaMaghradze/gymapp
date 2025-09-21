package com.gymapp.monitoring.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TraineeMetrics {

    private final Counter traineeCreatedCounter;

    public TraineeMetrics(MeterRegistry registry) {
        this.traineeCreatedCounter = Counter.builder("trainees_created")
                .description("Number of trainees created")
                .register(registry);
    }

    public void incrementTraineesCreated() {
        traineeCreatedCounter.increment();
    }
}
