package com.rest.gymapp.dto.response;

import com.rest.gymapp.dto.response.trainee.TraineeResponse;
import com.rest.gymapp.dto.response.trainer.TrainerResponse;
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
