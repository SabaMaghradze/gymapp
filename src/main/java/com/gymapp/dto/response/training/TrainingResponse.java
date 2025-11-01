package com.gymapp.dto.response.training;

import com.gymapp.dto.response.trainingtype.TrainingTypeResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TrainingResponse {

    private String traineeUsername;

    private String trainerUsername;

    private String trainingName;

    private LocalDate trainingDate;

    private Integer trainingDuration;
}
