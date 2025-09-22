package com.gymapp.service;

import com.gymapp.dto.request.trainee.TraineeActivationRequest;
import com.gymapp.dto.request.trainee.TraineeRegistrationRequest;
import com.gymapp.dto.request.trainee.TraineeUpdateRequest;
import com.gymapp.dto.request.trainee.UpdateTraineeTrainersRequest;
import com.gymapp.dto.response.RegistrationResponse;
import com.gymapp.dto.response.trainee.TraineeProfileResponse;
import com.gymapp.dto.response.trainee.TraineeUpdateResponse;
import com.gymapp.dto.response.trainer.TrainerResponseBasic;
import com.gymapp.dto.response.training.TrainingResponseForTrainee;

import java.time.LocalDate;
import java.util.List;

public interface TraineeService {

    TraineeProfileResponse getTraineeById(Long id, String transactionId);

    RegistrationResponse createTraineeProfile(TraineeRegistrationRequest req, String transactionId);

    TraineeProfileResponse getTraineeProfileByUsername(String username, String password, String transactionId);

    TraineeUpdateResponse updateTraineeProfile(TraineeUpdateRequest request, String username, String password, String transactionId);

    void activateDeactivateTrainee(TraineeActivationRequest req, String username, String password, String transactionId);

    void deleteTraineeProfile(String username, String password, String transactionId);

    List<TrainerResponseBasic> findNonAssignedTrainers(String traineeUsername, String password, String transactionId);

    List<TrainingResponseForTrainee> findTraineeTrainings(String username,
                                                          String password,
                                                          LocalDate fromDate,
                                                          LocalDate toDate,
                                                          String trainerName,
                                                          String trainingType,
                                                          String transactionId);

    List<TrainerResponseBasic> updateTraineeTrainers(UpdateTraineeTrainersRequest req, String username, String password, String transactionId);
}
