package com.rest.gymapp.dto.response.trainer;

import com.rest.gymapp.dto.response.TrainingTypeResponse;
import com.rest.gymapp.dto.response.trainee.TraineeResponseForGetTrainer;
import lombok.Data;

import java.util.Set;

@Data
public class TrainerUpdateResponse {

    private String username;

    private String firstName; // may remove

    private String lastName; // may remove

    private TrainingTypeResponse specialization;

    private Boolean isActive;

    private Set<TraineeResponseForGetTrainer> traineeResponses;

    public TrainerUpdateResponse(String username, String firstName, String lastName, TrainingTypeResponse specialization, Boolean isActive) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.isActive = isActive;
    }
}
