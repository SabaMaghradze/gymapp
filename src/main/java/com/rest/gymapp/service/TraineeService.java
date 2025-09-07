package com.rest.gymapp.service;

import com.rest.gymapp.dto.request.trainee.TraineeUpdateRequest;
import com.rest.gymapp.dto.response.RegistrationResponse;
import com.rest.gymapp.dto.response.trainee.TraineeProfileResponse;
import com.rest.gymapp.dto.response.trainee.TraineeResponse;
import com.rest.gymapp.model.Trainer;
import com.rest.gymapp.model.Training;

import java.time.LocalDate;
import java.util.List;


public interface TraineeService {

    RegistrationResponse createTraineeProfile(String firstName, String lastName,
                                              LocalDate dateOfBirth, String address);

    TraineeProfileResponse getTraineeProfileByUsername(String username, String password);

//    boolean changeTraineePassword(String username, String oldPassword, String newPassword);

    TraineeResponse updateTraineeProfile(TraineeUpdateRequest request, String password);

    boolean activateDeactivateTrainee(String username, String password, boolean active);

    void deleteTraineeProfile(String username, String password);

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
