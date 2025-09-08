package com.rest.gymapp.dto.response.trainee;

import com.rest.gymapp.dto.response.trainer.TrainerResponseBasic;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class TraineeProfileResponse {

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private String address;

    private Boolean isActive;

    private Set<TrainerResponseBasic> trainerResponses;

    public TraineeProfileResponse(String firstName, String lastName, LocalDate dateOfBirth, String address, Boolean isActive) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.isActive = isActive;
    }
}
