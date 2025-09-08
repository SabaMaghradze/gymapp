package com.rest.gymapp.dto.response;

import com.rest.gymapp.dto.response.trainer.TrainerProfileResponse;
import com.rest.gymapp.dto.response.training.TrainingResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@AllArgsConstructor
@Data
public class TrainingTypeResponse {

    private Long id;

    private String trainingTypeName;

    private Set<TrainingResponse> trainingResponses;

    private Set<TrainerProfileResponse> trainerProfileRespons;

    public TrainingTypeResponse(Long id, String trainingTypeName) {
        this.id = id;
        this.trainingTypeName = trainingTypeName;
    }
}
