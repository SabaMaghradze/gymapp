package com.rest.gymapp.dto.response.trainer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.rest.gymapp.dto.response.TrainingTypeResponse;
import com.rest.gymapp.dto.response.UserResponse;
import com.rest.gymapp.dto.response.trainee.TraineeResponse;
import lombok.Data;

import java.util.Set;

@Data
public class TrainerResponse {

    private String firstName; // may remove

    private String lastName; // may remove

    private String username; // may remove

    private TrainingTypeResponse specialization;

    private UserResponse userResponse; // remove if we keep firstname, lastname and username fields.

    @JsonBackReference
    Set<TraineeResponse> traineeResponses;

    public TrainerResponse(TrainingTypeResponse specialization, UserResponse userResponse) {
        this.specialization = specialization;
        this.userResponse = userResponse;
    }

    public TrainerResponse(String firstName, String lastName, String username, TrainingTypeResponse specialization) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.specialization = specialization;
    }
}
