package com.rest.gymapp.service;

import com.rest.gymapp.dto.request.trainer.TrainerUpdateRequest;
import com.rest.gymapp.dto.response.RegistrationResponse;
import com.rest.gymapp.dto.response.trainer.TrainerProfileResponse;
import com.rest.gymapp.dto.response.trainer.TrainerUpdateResponse;
import com.rest.gymapp.model.Training;
import com.rest.gymapp.model.TrainingType;
import java.time.LocalDate;
import java.util.List;

public interface TrainerService {

    RegistrationResponse createTrainerProfile(String firstName, String lastName,
                                              TrainingType specialization);

    TrainerProfileResponse getTrainerByUsername(String username, String password);

//    boolean changeTrainerPassword(String username, String oldPassword, String newPassword);

    TrainerUpdateResponse updateTrainerProfile(TrainerUpdateRequest req, String password);

    boolean activateDeactivateTrainer(String username, String password, boolean active);

    boolean deleteTrainerProfile(String username, String password);

    List<Training> getTrainerTrainingsByCriteria(
            String trainerUsername,
            String password,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName,
            String trainingTypeName
    );
}
