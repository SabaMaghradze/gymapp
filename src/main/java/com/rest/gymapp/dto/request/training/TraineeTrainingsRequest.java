package com.rest.gymapp.dto.request.training;

import com.rest.gymapp.model.TrainingType;

import java.time.LocalDate;

public record TraineeTrainingsRequest(
        LocalDate fromDate,
        LocalDate toDate,
        String trainerName,
        TrainingType trainingType
) {
}
