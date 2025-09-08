package com.rest.gymapp.dto.response.training;

import com.rest.gymapp.dto.response.TrainingTypeResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;


@AllArgsConstructor
@Data
public class TrainingResponse {

    private String trainingName;

    private LocalDate trainingDate;

    private TrainingTypeResponse trainingTypeResponse;

    private Number duration;

    private String trainerName;
}
