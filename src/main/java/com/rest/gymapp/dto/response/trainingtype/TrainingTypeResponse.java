package com.rest.gymapp.dto.response.trainingtype;

import lombok.Data;

@Data
public class TrainingTypeResponse {

    private Long id;

    private String trainingTypeName;

    public TrainingTypeResponse(Long id, String trainingTypeName) {
        this.id = id;
        this.trainingTypeName = trainingTypeName;
    }
}
