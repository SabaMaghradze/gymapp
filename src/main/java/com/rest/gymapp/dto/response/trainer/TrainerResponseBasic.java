package com.rest.gymapp.dto.response.trainer;

import com.rest.gymapp.dto.response.TrainingTypeResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TrainerResponseBasic {

    private String firstName;

    private String lastName;

    private String username;

    private TrainingTypeResponse trainingTypeResponse;
}
