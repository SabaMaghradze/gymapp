package com.rest.gymapp.dto.request.training;

import com.rest.gymapp.model.TrainingType;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record TraineeTrainingsRequest(
        @NotBlank String traineeUsername,
        LocalDate fromDate,
        LocalDate toDate,
        String trainerName,
        TrainingType trainingType
) {
}
