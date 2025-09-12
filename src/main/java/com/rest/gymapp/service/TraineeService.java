package com.rest.gymapp.service;

import com.rest.gymapp.dto.request.trainee.TraineeActivationRequest;
import com.rest.gymapp.dto.request.trainee.TraineeRegistrationRequest;
import com.rest.gymapp.dto.request.trainee.TraineeUpdateRequest;
import com.rest.gymapp.dto.request.trainee.UpdateTraineeTrainersRequest;
import com.rest.gymapp.dto.request.training.TraineeTrainingsRequest;
import com.rest.gymapp.dto.response.RegistrationResponse;
import com.rest.gymapp.dto.response.trainee.TraineeProfileResponse;
import com.rest.gymapp.dto.response.trainee.TraineeUpdateResponse;
import com.rest.gymapp.dto.response.trainer.TrainerResponseBasic;
import com.rest.gymapp.dto.response.training.TrainingResponseForTrainee;
import com.rest.gymapp.model.TrainingType;

import java.time.LocalDate;
import java.util.List;


public interface TraineeService {

    RegistrationResponse createTraineeProfile(TraineeRegistrationRequest req);

    TraineeProfileResponse getTraineeProfileByUsername(String username, String password);

    TraineeUpdateResponse updateTraineeProfile(TraineeUpdateRequest request, String username, String password);

    void activateDeactivateTrainee(TraineeActivationRequest req, String password);

    void deleteTraineeProfile(String username, String password);

    List<TrainerResponseBasic> findNonAssignedTrainers(String traineeUsername, String password);

    List<TrainingResponseForTrainee> findTraineeTrainings(String username,
                                                          String password,
                                                          LocalDate fromDate,
                                                          LocalDate toDate,
                                                          String trainerName,
                                                          String trainingType);

    List<TrainerResponseBasic> updateTraineeTrainers(UpdateTraineeTrainersRequest req, String username, String password);

//    boolean changeTraineePassword(String username, String oldPassword, String newPassword);
}
