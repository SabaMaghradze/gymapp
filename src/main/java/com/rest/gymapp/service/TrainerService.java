package com.rest.gymapp.service;

import com.rest.gymapp.dto.response.RegistrationResponse;
import com.rest.gymapp.dto.response.TrainerResponse;
import com.rest.gymapp.model.Trainer;
import com.rest.gymapp.model.Training;
import com.rest.gymapp.model.TrainingType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainerService {


    RegistrationResponse createTrainerProfile(String firstName, String lastName,
                                              TrainingType specialization);

    TrainerResponse getTrainerByUsername(String username, String password);

    boolean changeTrainerPassword(String username, String oldPassword, String newPassword);

    Optional<Trainer> updateTrainerProfile(String username, String password,
                                           String newFirstName, String newLastName,
                                           TrainingType newSpecialization, Boolean isActive);


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
