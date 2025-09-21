package com.gymapp.dto.request.training;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record TrainerTrainingsRequest(
        @NotBlank String trainerUsername,
        LocalDate fromDate,
        LocalDate toDate,
        String traineeName
) {
}
