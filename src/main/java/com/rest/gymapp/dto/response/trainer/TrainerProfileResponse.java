package com.rest.gymapp.dto.response.trainer;

import com.rest.gymapp.dto.response.trainingtype.TrainingTypeResponse;
import com.rest.gymapp.dto.response.UserResponse;
import com.rest.gymapp.dto.response.trainee.TraineeResponseForGetTrainer;
import lombok.Data;

import java.util.Set;

@Data
public class TrainerProfileResponse {

    private String firstName; // may remove

    private String lastName; // may remove

    private TrainingTypeResponse specialization;

    private Boolean isActive;

    private UserResponse userResponse; // remove if we keep firstname, lastname and username fields.

    Set<TraineeResponseForGetTrainer> traineeResponses;

    public TrainerProfileResponse(TrainingTypeResponse specialization, UserResponse userResponse) {
        this.specialization = specialization;
        this.userResponse = userResponse;
    }

    public TrainerProfileResponse(String firstName, String lastName, TrainingTypeResponse specialization, Boolean isActive) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.isActive = isActive;
    }
}
