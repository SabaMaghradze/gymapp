package com.gymapp.dto.response.trainee;

import com.gymapp.dto.response.trainer.TrainerResponseBasic;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@AllArgsConstructor
@Data
public class TraineeUpdateResponse {

    private String username;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private String address;

    private Boolean isActive;

    private Set<TrainerResponseBasic> trainerProfileResponse;

    public TraineeUpdateResponse(String username, String firstName, String lastName, LocalDate dateOfBirth, String address, Boolean isActive) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.isActive = isActive;
    }

    public TraineeUpdateResponse(String username, String firstName, String lastName, Boolean isActive, String address) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
        this.address = address;
    }
}
