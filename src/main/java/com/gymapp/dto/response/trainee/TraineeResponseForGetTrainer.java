package com.gymapp.dto.response.trainee;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TraineeResponseForGetTrainer {

    private String username;

    private String firstName;

    private String lastName;
}
