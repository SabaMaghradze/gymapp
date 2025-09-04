package com.rest.gymapp.service;


import com.rest.gymapp.model.Training;

import java.time.LocalDate;

public interface TrainingService {

    Training addTraining(
            String traineeUsername,
            String password,
            String trainerUsername,
            String trainingTypeName,
            LocalDate trainingDate
    );
}
