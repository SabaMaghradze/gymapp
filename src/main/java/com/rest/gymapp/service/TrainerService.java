package com.rest.gymapp.service;

import com.rest.gymapp.dto.request.trainer.TrainerActivationRequest;
import com.rest.gymapp.dto.request.trainer.TrainerUpdateRequest;
import com.rest.gymapp.dto.request.training.TrainerTrainingsRequest;
import com.rest.gymapp.dto.response.RegistrationResponse;
import com.rest.gymapp.dto.response.trainer.TrainerProfileResponse;
import com.rest.gymapp.dto.response.trainer.TrainerUpdateResponse;
import com.rest.gymapp.dto.response.training.TrainingResponseForTrainer;
import com.rest.gymapp.model.TrainingType;
import java.util.List;

public interface TrainerService {

    RegistrationResponse createTrainerProfile(String firstName, String lastName,
                                              TrainingType specialization);

    TrainerProfileResponse getTrainerByUsername(String username, String password);

    TrainerUpdateResponse updateTrainerProfile(TrainerUpdateRequest req, String password);

    void activateDeactivateTrainer(TrainerActivationRequest req, String password);

    List<TrainingResponseForTrainer> findTrainerTrainingsByCriteria(TrainerTrainingsRequest req, String password);

//    boolean changeTrainerPassword(String username, String oldPassword, String newPassword);
}
