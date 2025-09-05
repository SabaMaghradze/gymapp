package com.rest.gymapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@AllArgsConstructor
@Data
public class TraineeResponse {

    private Long id;

    private LocalDate dateOfBirth;

    private String address;

    private UserResponse userResponse;

    private Set<TrainerResponse> trainerResponses;

    private Set<TrainingResponse> trainingResponses;

    public TraineeResponse(Long id, LocalDate dateOfBirth, String address, UserResponse userResponse) {
        this.id = id;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.userResponse = userResponse;
    }
}
