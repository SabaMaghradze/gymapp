package com.gymapp.dto.response.trainer;

import com.gymapp.dto.response.trainingtype.TrainingTypeResponse;
import com.gymapp.dto.response.trainee.TraineeResponseForGetTrainer;
import lombok.Data;

import java.util.Set;

@Data
public class TrainerProfileResponse {

    private String firstName; // may remove

    private String lastName; // may remove

    private TrainingTypeResponse specialization;

    private Boolean isActive;

    Set<TraineeResponseForGetTrainer> traineeResponses;

    public TrainerProfileResponse(String firstName, String lastName, TrainingTypeResponse specialization, Boolean isActive) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.isActive = isActive;
    }
}
