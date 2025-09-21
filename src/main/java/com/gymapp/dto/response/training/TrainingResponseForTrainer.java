package com.gymapp.dto.response.training;

import com.gymapp.dto.response.trainingtype.TrainingTypeResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@AllArgsConstructor
@Data
public class TrainingResponseForTrainer {

    private String trainingName;

    private LocalDate trainingDate;

    private TrainingTypeResponse trainingTypeResponse;

    private Number duration;

    private String traineeName;
}
