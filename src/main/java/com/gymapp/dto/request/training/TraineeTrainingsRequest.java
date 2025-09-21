package com.gymapp.dto.request.training;

import com.gymapp.model.TrainingType;

import java.time.LocalDate;

public record TraineeTrainingsRequest(
        LocalDate fromDate,
        LocalDate toDate,
        String trainerName,
        TrainingType trainingType
) {
}
