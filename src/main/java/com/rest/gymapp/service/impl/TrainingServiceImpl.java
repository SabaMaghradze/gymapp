package com.rest.gymapp.service.impl;

import com.rest.gymapp.dto.request.TrainingRequest;
import com.rest.gymapp.exception.ResourceNotFoundException;
import com.rest.gymapp.exception.UserNotFoundException;
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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);

    private final TrainingRepository trainingRepository;
    private final AuthenticationService authenticationService;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public void addTraining(TrainingRequest req) {

        logger.info("Attempting to add training for trainee [{}] with trainer [{}] and type [{}] on [{}]",
                req.traineeUsername(), req.trainerUsername(), req.trainingTypeName(), req.trainingDate());

        authenticationService.authenticateTrainee(req.traineeUsername(), req.password());

        Trainee trainee = traineeRepository.findByUsername(req.traineeUsername())
                .orElseThrow(() -> new UserNotFoundException("Trainee not found: " + req.traineeUsername()));

        Trainer trainer = trainerRepository.findByUsername(req.trainerUsername())
                .orElseThrow(() -> new UserNotFoundException("Trainer not found: " + req.trainerUsername()));

        TrainingType trainingType = trainerRepository
                .findByUsername(req.trainerUsername()).get()
                .getSpecialization();

        if (trainingType.getTrainingTypeName() != req.trainingTypeName() || trainingType == null) {
            throw new ResourceNotFoundException("This trainer does not offer that service");
        }

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);
        training.setTrainingDate(req.trainingDate());

        Training savedTraining = trainingRepository.save(training);

        logger.info("Training successfully created with ID [{}] for trainee [{}]",
                savedTraining.getId(), req.traineeUsername());
    }
}
