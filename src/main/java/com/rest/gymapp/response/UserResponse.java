package com.rest.gymapp.response;

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
}
