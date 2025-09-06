package com.rest.gymapp.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record TrainingRequest(
        @NotBlank String traineeUsername,
        @NotBlank String trainerUsername,
        @NotBlank String trainingTypeName,
        @Future LocalDate trainingDate,
        @Positive Number trainingDuration,
        @NotBlank String password
) {
}
