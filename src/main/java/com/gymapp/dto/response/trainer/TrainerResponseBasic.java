package com.gymapp.dto.response.trainer;

import com.gymapp.dto.response.trainingtype.TrainingTypeResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TrainerResponseBasic {

    private String firstName;

    private String lastName;

    private String username;

    private TrainingTypeResponse trainingTypeResponse;

    public TrainerResponseBasic(String username) {
        this.username = username;
    }
}
