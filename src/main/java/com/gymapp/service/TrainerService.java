package com.gymapp.service;

import com.gymapp.dto.request.trainer.TrainerActivationRequest;
import com.gymapp.dto.request.trainer.TrainerRegistrationRequest;
import com.gymapp.dto.request.trainer.TrainerUpdateRequest;
import com.gymapp.dto.response.RegistrationResponse;
import com.gymapp.dto.response.trainer.TrainerProfileResponse;
import com.gymapp.dto.response.trainer.TrainerUpdateResponse;
import com.gymapp.dto.response.training.TrainingResponseForTrainer;
import com.gymapp.model.Trainer;

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
