package com.gymapp.service.impl;

import com.gymapp.producer.WorkloadMessageProducer;
import com.gymapp.dto.request.training.TrainingRegistrationRequest;
import com.gymapp.common.WorkloadRequest;
import com.gymapp.dto.response.training.TrainingResponse;
import com.gymapp.exception.WorkloadServiceUnavailableException;
import com.gymapp.exception.resource.ResourceNotFoundException;
import com.gymapp.exception.user.UserNotFoundException;
import com.gymapp.model.Trainee;
import com.gymapp.model.Trainer;
import com.gymapp.model.Training;
import com.gymapp.model.TrainingType;
import com.gymapp.repository.TraineeRepository;
import com.gymapp.repository.TrainerRepository;
import com.gymapp.repository.TrainingRepository;
import com.gymapp.repository.TrainingTypeRepository;
import com.gymapp.service.TrainingService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final WorkloadMessageProducer workloadMessageProducer;

    @CircuitBreaker(name = "workloadService", fallbackMethod = "addTrainingFallback")
    @Transactional
    public void addTraining(TrainingRegistrationRequest req, String transactionId) {

        logger.info("[{}] Attempting to add training for trainee [{}] with trainer [{}] and type [{}] on [{}] that will last [{}]",
                transactionId,
                req.traineeUsername(),
                req.trainerUsername(),
                req.trainingName(), req.trainingDate(), req.duration());

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
        training.setTrainingDuration(req.duration());

        Training savedTraining = trainingRepository.save(training);

        trainee.getTrainers().add(trainer);
        trainer.getTrainees().add(trainee);
        traineeRepository.save(trainee);

        WorkloadRequest workloadRequest = new WorkloadRequest(
                trainer.getUser().getUsername(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getUser().getIsActive(),
                training.getTrainingDate(),
                training.getTrainingDuration(),
                "ADD"
        );

        logger.info("[{}] Sending workload for trainer: {}", transactionId, workloadRequest.getTrainerUsername());
        workloadMessageProducer.sendWorkload(workloadRequest);

        logger.info("[{}] Training successfully created with ID [{}] for trainee [{}]", transactionId,
                savedTraining.getId(), req.traineeUsername());
    }

    // Resilience4j locates a fallback whose last parameter type matches the thrown exception in client config.
    private void addTrainingFallback(TrainingRegistrationRequest req,
                                     String transactionId,
                                     ResponseStatusException ex) {
        logger.warn("[{}] Workload service returned {}: {}",
                transactionId, ex.getStatusCode(), ex.getReason());

        throw new ResponseStatusException(ex.getStatusCode(),
                ex.getReason(),
                ex);
    }

    // generic fallback
    private void addTrainingFallback(TrainingRegistrationRequest req,
                                     String transactionId,
                                     Throwable ex) {
        logger.error("[{}] Workload service unavailable", transactionId, ex);
        throw new WorkloadServiceUnavailableException(
                "Workload service is unavailable for the time being.");
    }

    @Override
    @CircuitBreaker(name = "workloadService", fallbackMethod = "cancelTrainingFallback")
    public void cancelTraining(Long trainingId, String transactionId) {

        logger.info("[{}] attempting to delete training with ID: {}", trainingId, trainingId);

        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new ResourceNotFoundException("Training not found"));

        Trainer trainer = training.getTrainer();

        WorkloadRequest workloadRequest = new WorkloadRequest(
                trainer.getUser().getUsername(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getUser().getIsActive(),
                training.getTrainingDate(),
                training.getTrainingDuration(),
                "CANCEL"
        );

        logger.info("[{}] Sending cancellation workload for trainer: {}", transactionId, workloadRequest.getTrainerUsername());

        workloadMessageProducer.sendWorkload(workloadRequest);

        trainingRepository.deleteById(trainingId);
    }

    public void cancelTrainingFallback(Long trainingId, String transactionId, Exception ex) {
        logger.info("[{}] Fallback method for cancel training has been triggered", transactionId);
        throw new WorkloadServiceUnavailableException("Workload service is unavailable for the time being.");
    }

    @Override
    public List<TrainingResponse> getAllTrainings(String transactionId) {

        logger.info("[{}] Fetching all trainings", transactionId);

        List<Training> trainings = trainingRepository.findAll();

        if (trainings.isEmpty()) {
            throw new ResourceNotFoundException("Trainings list is empty");
        }

        return trainings.stream().map(training -> new TrainingResponse(
                        training.getTrainee().getUser().getUsername(),
                        training.getTrainer().getUser().getUsername(),
                        training.getTrainingName(),
                        training.getTrainingDate(),
                        training.getTrainingDuration()
                ))
                .toList();
    }
}
