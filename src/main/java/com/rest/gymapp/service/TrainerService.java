package com.rest.gymapp.service;

import com.rest.gymapp.dto.request.trainer.TrainerActivationRequest;
import com.rest.gymapp.dto.request.trainer.TrainerRegistrationRequest;
import com.rest.gymapp.dto.request.trainer.TrainerUpdateRequest;
import com.rest.gymapp.dto.request.training.TrainerTrainingsRequest;
import com.rest.gymapp.dto.response.RegistrationResponse;
import com.rest.gymapp.dto.response.trainer.TrainerProfileResponse;
import com.rest.gymapp.dto.response.trainer.TrainerUpdateResponse;
import com.rest.gymapp.dto.response.training.TrainingResponseForTrainer;
import com.rest.gymapp.model.Trainer;

import java.time.LocalDate;
import java.util.List;

public interface TrainerService {

    List<Trainer> getAllTrainers();

    RegistrationResponse createTrainerProfile(TrainerRegistrationRequest req, String transactionId);

    TrainerProfileResponse getTrainerByUsername(String username, String password, String transactionId);

    TrainerUpdateResponse updateTrainerProfile(TrainerUpdateRequest req, String username, String password, String transactionId);

    void activateDeactivateTrainer(TrainerActivationRequest req, String username, String password, String transactionId);

    List<TrainingResponseForTrainer> findTrainerTrainingsByCriteria(String username,
                                                                    String password,
                                                                    LocalDate fromDate,
                                                                    LocalDate toDate,
                                                                    String traineeName,
                                                                    String transactionId
    );
}
