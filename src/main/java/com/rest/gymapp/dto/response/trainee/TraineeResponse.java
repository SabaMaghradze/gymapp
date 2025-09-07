package com.rest.gymapp.dto.response.trainee;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.rest.gymapp.dto.response.trainer.TrainerResponse;
import com.rest.gymapp.dto.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@AllArgsConstructor
@Data
public class TraineeResponse {

    private String username;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private String address;

    private Boolean isActive;

    @JsonBackReference
    private Set<TrainerResponse> trainerResponses;

    private UserResponse userResponse; // remove if we keep the fields also existent in user response.

    public TraineeResponse(LocalDate dateOfBirth, String address, UserResponse userResponse) {
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.userResponse = userResponse;
    }

    public TraineeResponse(String username, String firstName, String lastName, LocalDate dateOfBirth, String address, Boolean isActive) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.isActive = isActive;
    }
}
