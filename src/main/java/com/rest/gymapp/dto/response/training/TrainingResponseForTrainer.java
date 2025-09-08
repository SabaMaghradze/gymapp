package com.rest.gymapp.dto.response.training;

import com.rest.gymapp.dto.response.trainingtype.TrainingTypeResponse;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
public class TrainingResponseForTrainer {

    private String trainingName;

    private LocalDate trainingDate;

    private TrainingTypeResponse trainingTypeResponse;

    private Number duration;

    private String traineeName;
}
