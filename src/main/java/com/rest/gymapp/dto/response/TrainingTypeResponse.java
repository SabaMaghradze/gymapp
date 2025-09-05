package com.rest.gymapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@AllArgsConstructor
@Data
public class TrainingTypeResponse {

    private Long id;

    private String trainingTypeName;

    private Set<TrainingResponse> trainingResponses;

    private Set<TrainerResponse> trainerResponses;

    public TrainingTypeResponse(Long id, String trainingTypeName) {
        this.id = id;
        this.trainingTypeName = trainingTypeName;
    }
}
