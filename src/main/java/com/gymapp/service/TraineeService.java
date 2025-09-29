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

    TraineeProfileResponse getTraineeProfileByUsername(String username, String transactionId);

    TraineeUpdateResponse updateTraineeProfile(TraineeUpdateRequest request, Long id, String transactionId);

    void activateDeactivateTrainee(TraineeActivationRequest req, Long id, String transactionId);

    void deleteTraineeProfile(Long id, String transactionId);

    List<TrainerResponseBasic> findNonAssignedTrainers(Long id, String transactionId);

    List<TrainingResponseForTrainee> findTraineeTrainings(Long id,
                                                          LocalDate fromDate,
                                                          LocalDate toDate,
                                                          String trainerName,
                                                          String trainingType,
                                                          String transactionId);

    List<TrainerResponseBasic> updateTraineeTrainers(Long id, UpdateTraineeTrainersRequest req, String transactionId);
}
