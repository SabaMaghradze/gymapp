package com.rest.gymapp.service.impl;

import com.rest.gymapp.model.Trainee;
import com.rest.gymapp.model.Trainer;
import com.rest.gymapp.model.Training;
import com.rest.gymapp.model.TrainingType;
import com.rest.gymapp.repository.TraineeRepository;
import com.rest.gymapp.repository.TrainerRepository;
import com.rest.gymapp.repository.TrainingRepository;
import com.rest.gymapp.service.AuthenticationService;
import com.rest.gymapp.service.TraineeService;
import com.rest.gymapp.service.TrainingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);

    private final TrainingRepository trainingRepository;
    private final AuthenticationService authenticationService;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public Training addTraining(
            String traineeUsername,
            String password,
            String trainerUsername,
            String trainingTypeName,
            LocalDate trainingDate
    ) {
        logger.info("Attempting to add training for trainee [{}] with trainer [{}] and type [{}] on [{}]",
                traineeUsername, trainerUsername, trainingTypeName, trainingDate);

        try {
            if (!authenticationService.authenticateTrainee(traineeUsername, password)) {
                logger.warn("Authentication failed for trainee: {}", traineeUsername);
                throw new SecurityException("Authentication failed for trainee: " + traineeUsername);
            }

            // Ensure trainee exists
            Trainee trainee = traineeRepository.findByUsername(traineeUsername)
                    .orElseThrow(() -> new IllegalArgumentException("Trainee not found: " + traineeUsername));

            // Ensure trainer exists
            Trainer trainer = trainerRepository.findByUsername(trainerUsername)
                    .orElseThrow(() -> new IllegalArgumentException("Trainer not found: " + trainerUsername));


            TrainingType trainingType = trainerRepository
                    .findByUsername(trainerUsername).get()
                    .getSpecialization();

            if (trainingType.getTrainingTypeName() != trainingTypeName || trainingType == null) {
                throw new IllegalArgumentException("This trainer does not offer that service");
            }


            // Validate date
            if (trainingDate == null || trainingDate.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Training date must be in the future");
            }

            Training training = new Training();
            training.setTrainee(trainee);
            training.setTrainer(trainer);
            training.setTrainingType(trainingType);
            training.setTrainingDate(trainingDate);

            Training savedTraining = trainingRepository.save(training);

            logger.info("Training successfully created with ID [{}] for trainee [{}]",
                    savedTraining.getId(), traineeUsername);

            return savedTraining;

        } catch (Exception e) {
            logger.error("Failed to add training for trainee [{}]", traineeUsername, e);
            throw new RuntimeException("Error while adding training", e);
        }
    }

}
