package com.rest.gymapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String username;

    private Boolean isActive;

    private TraineeResponse traineeResponse;

    private TrainerResponse trainerResponse;

    public UserResponse(Long id, String firstName, String lastName, String username, Boolean isActive) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.isActive = isActive;
    }
}
