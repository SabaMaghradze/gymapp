package com.rest.gymapp.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@AllArgsConstructor
@Data
public class TrainerResponse {

    private Long ID;

    private TrainingTypeResponse specialization;

    private UserResponse userResponse;

    private Set<TraineeResponse> traineeResponses;

    private Set<TrainingResponse> trainingResponses;
}
