package com.rest.gymapp.service;

import com.rest.gymapp.dto.response.RegistrationResponse;
import com.rest.gymapp.model.Trainee;
import com.rest.gymapp.model.Trainer;
import com.rest.gymapp.model.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface TraineeService {

    RegistrationResponse createTraineeProfile(String firstName, String lastName,
                                              LocalDate dateOfBirth, String address);

    Optional<Trainee> getTraineeProfileByUsername(String username, String password);

    boolean changeTraineePassword(String username, String oldPassword, String newPassword);

    Optional<Trainee> updateTraineeProfile(String username, String password,
                                           String newFirstName, String newLastName,
                                           LocalDate newDateOfBirth, String newAddress,
                                           Boolean isActive);

    boolean activateDeactivateTrainee(String username, String password, boolean active);

    boolean deleteTraineeProfile(String username, String password);

    List<Trainer> findNonAssignedTrainers(String traineeUsername, String password);

    List<Training> getTraineeTrainingsByCriteria(
            String traineeUsername,
            String password,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            String trainingTypeName
    );
}
