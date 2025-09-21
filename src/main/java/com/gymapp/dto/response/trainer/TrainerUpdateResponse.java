package com.gymapp.dto.response.trainer;

import com.gymapp.dto.response.trainingtype.TrainingTypeResponse;
import com.gymapp.dto.response.trainee.TraineeResponseForGetTrainer;
import lombok.Data;

import java.util.Set;

@Data
public class TrainerUpdateResponse {

    private String username;

    private String firstName;

    private String lastName;

    private TrainingTypeResponse trainingType;

    private Boolean isActive;

    private Set<TraineeResponseForGetTrainer> traineeResponses;

    public TrainerUpdateResponse(String username, String firstName, String lastName, TrainingTypeResponse trainingType, Boolean isActive) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.trainingType = trainingType;
        this.isActive = isActive;
    }
}
