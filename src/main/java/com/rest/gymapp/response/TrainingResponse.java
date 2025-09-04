package com.rest.gymapp.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@AllArgsConstructor
@Data
public class TrainingResponse {

    private Long id;

    private TraineeResponse traineeResponse;

    private TrainerResponse trainerResponse;

    private TrainingTypeResponse trainingTypeResponse;

    private String trainingName;

    private LocalDate trainingDate;

    private Number trainingDuration;
}
