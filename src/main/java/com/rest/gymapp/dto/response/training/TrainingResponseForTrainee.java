package com.rest.gymapp.dto.response.training;

import com.rest.gymapp.dto.response.trainingtype.TrainingTypeResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;


@AllArgsConstructor
@Data
public class TrainingResponseForTrainee {

    private String trainingName;

    private LocalDate trainingDate;

    private TrainingTypeResponse trainingTypeResponse;

    private Number duration;

    private String trainerName;
}
