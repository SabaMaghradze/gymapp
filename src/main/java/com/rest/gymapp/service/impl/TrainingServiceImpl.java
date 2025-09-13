package com.rest.gymapp.service.impl;

import com.rest.gymapp.dto.request.training.TrainingRegistrationRequest;
import com.rest.gymapp.exception.ResourceNotFoundException;
import com.rest.gymapp.exception.UserNotFoundException;
import com.rest.gymapp.model.Trainee;
import com.rest.gymapp.model.Trainer;
import com.rest.gymapp.model.Training;
import com.rest.gymapp.model.TrainingType;
import com.rest.gymapp.repository.TraineeRepository;
import com.rest.gymapp.repository.TrainerRepository;
import com.rest.gymapp.repository.TrainingRepository;
import com.rest.gymapp.repository.TrainingTypeRepository;
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
    private final TrainingTypeRepository trainingTypeRepository;

    public void addTraining(TrainingRegistrationRequest req, String username, String password, String transactionId) {

        logger.info("[{}] Attempting to add training for trainee [{}] with trainer [{}] and type [{}] on [{}] that will last [{}]",
                transactionId,
                req.traineeUsername(),
                req.trainerUsername(),
                req.trainingName(), req.trainingDate(), req.duration());

        // I am considering that trainee himself/herself should be able to register the
        // training of their choice, if trainer should have that privilege,
        // then we will authenticate trainer instead of trainee.
        authenticationService.authenticateTrainee(username, password);

        Trainee trainee = traineeRepository.findByUserUsername(req.traineeUsername())
                .orElseThrow(() -> new UserNotFoundException("Trainee not found: " + req.traineeUsername()));

        Trainer trainer = trainerRepository.findByUserUsername(req.trainerUsername())
                .orElseThrow(() -> new UserNotFoundException("Trainer not found: " + req.trainerUsername()));

        TrainingType trainingType = trainingTypeRepository.findByTrainingTypeName(req.trainingName())
                .orElseThrow(() -> new ResourceNotFoundException("No such training exists"));

        if (!trainer.getSpecialization().getTrainingTypeName().equals(req.trainingName())) {
            throw new ResourceNotFoundException("This trainer does not offer that service");
        }

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);
        training.setTrainingName(req.trainingName());
        training.setTrainingDate(req.trainingDate());
        training.setTrainingDuration(req.duration().intValue());

        Training savedTraining = trainingRepository.save(training);

        trainee.getTrainers().add(trainer);
        trainer.getTrainees().add(trainee);
        traineeRepository.save(trainee);

        logger.info("[{}] Training successfully created with ID [{}] for trainee [{}]", transactionId,
                savedTraining.getId(), req.traineeUsername());
    }
}
