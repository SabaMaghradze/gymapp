package com.rest.gymapp.service.impl;

import com.rest.gymapp.dto.request.trainee.TraineeUpdateRequest;
import com.rest.gymapp.dto.request.trainee.UpdateTraineeTrainersRequest;
import com.rest.gymapp.dto.request.training.TraineeTrainingsRequest;
import com.rest.gymapp.dto.response.RegistrationResponse;
import com.rest.gymapp.dto.response.trainee.TraineeProfileResponse;
import com.rest.gymapp.dto.response.trainee.TraineeUpdateResponse;
import com.rest.gymapp.dto.response.trainer.TrainerResponseBasic;
import com.rest.gymapp.dto.response.training.TrainingResponse;
import com.rest.gymapp.exception.ResourceNotFoundException;
import com.rest.gymapp.exception.UserNotFoundException;
import com.rest.gymapp.model.Trainee;
import com.rest.gymapp.model.Trainer;
import com.rest.gymapp.model.Training;
import com.rest.gymapp.model.User;
import com.rest.gymapp.repository.TraineeRepository;
import com.rest.gymapp.repository.UserRepository;
import com.rest.gymapp.service.AuthenticationService;
import com.rest.gymapp.service.TraineeService;
import com.rest.gymapp.utils.CredentialsGenerator;
import com.rest.gymapp.utils.Mappers;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);

    private final TraineeRepository traineeRepository;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final CredentialsGenerator credentialsGenerator;
    private final Mappers mappers;

    public RegistrationResponse createTraineeProfile(String firstName, String lastName,
                                                     LocalDate dateOfBirth, String address) {

        logger.info("Registering trainee profile for: {} {}", firstName, lastName);

        String username = credentialsGenerator.generateUsername(firstName, lastName, userRepository);
        String password = credentialsGenerator.generatePassword();

        User user = new User();
        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());
        user.setUsername(username);
        user.setPassword(password);
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        Trainee trainee = new Trainee();
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        trainee.setUser(savedUser);

        Trainee savedTrainee = traineeRepository.save(trainee);

        logger.info("Successfully created trainee profile for username: {}", username);
        return new RegistrationResponse(username, password);
    }

    public TraineeProfileResponse getTraineeProfileByUsername(String username, String password) {

        logger.info("Getting trainee profile for username: {}", username);

        authenticationService.authenticateTrainee(username, password);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found"));

        return mappers.getTraineeProfileResponse(trainee);
    }

//    public boolean changeTraineePassword(String username, String oldPassword, String newPassword) {
//        logger.info("Changing password for trainer username: {}", username);
//
//        try {
//            Optional<Trainee> traineeOpt = traineeRepository.findByUsername(username);
//
//            if (!traineeOpt.isPresent()) {
//                logger.warn("Trainer not found for username: {}", username);
//                return false;
//            }
//
//            Trainee trainee = traineeOpt.get();
//
//            // Verify old password
//            if (!authenticationService.authenticateTrainer(username, oldPassword)) {
//                logger.warn("Old password verification failed for trainer username: {}", username);
//                return false;
//            }
//
//            // Validate new password
//            if (newPassword == null || newPassword.trim().isEmpty()) {
//                logger.warn("New password cannot be empty");
//                return false;
//            }
//
//            trainee.getUser().setPassword(newPassword.trim());
//            userRepository.save(trainee.getUser());
//
//            logger.info("Successfully changed password for trainer username: {}", username);
//            return true;
//
//        } catch (Exception e) {
//            logger.error("Error changing password for trainer username: {}", username, e);
//            throw new RuntimeException("Failed to change password", e);
//        }
//    }

    public boolean activateDeactivateTrainee(String username, String password, boolean active) {
        logger.info("{} trainer with username: {}", active ? "Activating" : "Deactivating", username);

        try {
            if (!authenticationService.authenticateTrainer(username, password)) {
                logger.warn("Authentication failed for trainer username: {}", username);
                return false;
            }

            Optional<Trainee> traineeOpt = traineeRepository.findByUsername(username);
            if (!traineeOpt.isPresent()) {
                logger.warn("Trainer not found for username: {}", username);
                return false;
            }

            Trainee trainee = traineeOpt.get();

            // Check if already in desired state (non-idempotent check)
            if (trainee.getUser().getIsActive() == active) {
                logger.warn("Trainer is already {}", active ? "active" : "inactive");
                return false;
            }

            trainee.getUser().setIsActive(active);
            userRepository.save(trainee.getUser());

            logger.info("Successfully {} trainer with username: {}",
                    active ? "activated" : "deactivated", username);
            return true;

        } catch (Exception e) {
            logger.error("Error {} trainer with username: {}",
                    active ? "activating" : "deactivating", username, e);
            throw new RuntimeException("Failed to update trainer status", e);
        }
    }

    public TraineeUpdateResponse updateTraineeProfile(TraineeUpdateRequest req, String password) {

        logger.info("Updating trainer profile for username: {}", req.username());

        authenticationService.authenticateTrainee(req.username(), password);

        Trainee trainee = traineeRepository.findByUsername(req.username())
                .orElseThrow(() -> new UserNotFoundException("Trainee not found"));

        User user = trainee.getUser();

        user.setFirstName(req.firstName().trim());
        user.setLastName(req.lastName().trim());
        user.setIsActive(req.isActive());

        if (req.dateOfBirth() != null) {
            trainee.setDateOfBirth(req.dateOfBirth());
        }
        if (req.address() != null && !req.address().trim().isEmpty()) {
            trainee.setAddress(req.address().trim());
        }

        userRepository.save(user);
        Trainee updatedTrainee = traineeRepository.save(trainee);

        logger.info("Successfully updated trainer profile for username: {}", req.username());

        return mappers.getTraineeUpdateResponse(updatedTrainee);
    }

    public void deleteTraineeProfile(String username, String password) {

        logger.info("Deleting trainee profile for username: {}", username);

        authenticationService.authenticateTrainee(username, password);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found"));

        traineeRepository.delete(trainee);

        logger.info("Successfully deleted trainee profile and associated trainings for username: {}", username);
    }

    public List<TrainerResponseBasic> findNonAssignedTrainers(String traineeUsername, String password) {

        logger.info("Searching for all the trainers that are not assigned to trainee: {}", traineeUsername);

        authenticationService.authenticateTrainee(traineeUsername, password);

        Trainee trainee = traineeRepository.findByUsername(traineeUsername)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found"));

        List<Trainer> trainers = traineeRepository.findTrainersNotAssignedToTrainee(traineeUsername);

        if (trainers == null || trainers.isEmpty()) {
            logger.warn("No trainers found: {}", traineeUsername);
            throw new ResourceNotFoundException("Failed to find trainers");
        }

        logger.info("Successfully fetched all the trainers: {}", traineeUsername);

        List<TrainerResponseBasic> responses = new ArrayList<>();

        for (Trainer trainer : trainers) {
            responses.add(mappers.getTrainerResponseBasic(trainer));
        }

        return responses;
    }

    public List<TrainingResponse> findTraineeTrainings(TraineeTrainingsRequest req, String password) {

        logger.info("Fetching trainings for trainee: {}", req.traineeUsername());

        authenticationService.authenticateTrainee(req.traineeUsername(), password);

        Trainee trainee = traineeRepository.findByUsername(req.traineeUsername())
                .orElseThrow(() -> new UserNotFoundException("Failed to find user"));

        List<Training> trainings = traineeRepository.findTrainingsByTraineeUsernameWithCriteria(
                req.traineeUsername(), req.fromDate(), req.toDate(), req.trainerName(), req.trainingType().getTrainingTypeName()
        );

        if (trainings == null || trainings.isEmpty()) {
            logger.info("No trainings found for trainee [{}] with given criteria", req.traineeUsername());
            throw new ResourceNotFoundException("Failed to find trainings for the user");
;        }

        logger.info("Found {} trainings for trainee [{}]", trainings.size(), req.traineeUsername());
        return trainings.stream()
                .map(mappers::getTrainingResponse)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<TrainerResponseBasic> updateTraineeTrainers(UpdateTraineeTrainersRequest req, String password) {

        logger.info("Fetching trainers of trainee: {}", req.username());

        authenticationService.authenticateTrainee(req.username(), password);

        Trainee trainee = traineeRepository.findByUsername(req.username())
                .orElseThrow(() -> new UserNotFoundException("Trainee not found"));

        Set<Trainer> existingTrainers = trainee.getTrainers();

        if (existingTrainers.isEmpty()) {
            throw new ResourceNotFoundException("Failed to find trainers for this trainee");
        }

        return existingTrainers.stream()
                .map(mappers::getTrainerResponseBasic)
                .collect(Collectors.toUnmodifiableList());
    }
}
