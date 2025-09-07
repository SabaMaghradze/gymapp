package com.rest.gymapp.service.impl;

import com.rest.gymapp.dto.request.trainer.TrainerUpdateRequest;
import com.rest.gymapp.dto.response.RegistrationResponse;
import com.rest.gymapp.dto.response.trainer.TrainerResponse;
import com.rest.gymapp.exception.UserNotFoundException;
import com.rest.gymapp.model.*;
import com.rest.gymapp.repository.TrainerRepository;
import com.rest.gymapp.repository.UserRepository;
import com.rest.gymapp.service.AuthenticationService;
import com.rest.gymapp.service.TrainerService;
import com.rest.gymapp.utils.CredentialsGenerator;
import com.rest.gymapp.utils.Mappers;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);

    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final CredentialsGenerator credentialsGenerator;
    private final Mappers mappers;

    public RegistrationResponse createTrainerProfile(String firstName, String lastName,
                                                     TrainingType specialization) {

        logger.info("Creating trainer profile for: {} {}", firstName, lastName);

        String username = credentialsGenerator.generateUsername(firstName, lastName, userRepository);
        String password = credentialsGenerator.generatePassword();

        User user = new User();
        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());
        user.setUsername(username);
        user.setPassword(password);
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        Trainer trainer = new Trainer();
        trainer.setSpecialization(specialization);
        trainer.setUser(savedUser);

        Trainer savedTrainer = trainerRepository.save(trainer);

        logger.info("Successfully created trainer profile for username: {}", username);
        return new RegistrationResponse(username, password);
    }

    public TrainerResponse getTrainerByUsername(String username, String password) {

        logger.info("Getting trainer profile for username: {}", username);

        authenticationService.authenticateTrainee(username, password);

        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainer not found"));

        return mappers.getTrainerResponse(trainer);
    }

//    @Transactional
//    public boolean changeTrainerPassword(String username, String oldPassword, String newPassword) {
//        logger.info("Changing password for trainer username: {}", username);
//
//        if (!authenticationService.authenticateTrainer(username, oldPassword)) {
//            logger.warn("Authentication failed for trainer username: {}", username);
//            return false;
//        }
//
//        try {
//            Optional<Trainer> trainerOpt = trainerRepository.findByUsername(username);
//
//            if (!trainerOpt.isPresent()) {
//                logger.warn("Trainer not found for username: {}", username);
//                return false;
//            }
//
//            Trainer trainer = trainerOpt.get();
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
//            trainer.getUser().setPassword(newPassword.trim());
//            userRepository.save(trainer.getUser());
//
//            logger.info("Successfully changed password for trainer username: {}", username);
//            return true;
//
//        } catch (Exception e) {
//            logger.error("Error changing password for trainer username: {}", username, e);
//            throw new RuntimeException("Failed to change password", e);
//        }
//    }

    @Transactional
    public TrainerResponse updateTrainerProfile(TrainerUpdateRequest req, String password) {

        logger.info("Updating trainer profile for username: {}", req.username());

        authenticationService.authenticateTrainer(req.username(), password);

        Trainer trainer = trainerRepository.findByUsername(req.username())
                .orElseThrow(() -> new UserNotFoundException("Trainer not found"));

        User user = trainer.getUser();

        user.setFirstName(req.firstName().trim());
        user.setLastName(req.lastName().trim());
        user.setIsActive(req.isActive());
        trainer.setSpecialization(req.specialization());

        userRepository.save(user);
        Trainer updatedTrainer = trainerRepository.save(trainer);

        logger.info("Successfully updated trainer profile for username: {}", req.username());
        return mappers.getTrainerResponse(updatedTrainer);
    }

    @Transactional
    public boolean activateDeactivateTrainer(String username, String password, boolean active) {
        logger.info("{} trainer with username: {}", active ? "Activating" : "Deactivating", username);

        try {
            if (!authenticationService.authenticateTrainer(username, password)) {
                logger.warn("Authentication failed for trainer username: {}", username);
                return false;
            }

            Optional<Trainer> trainerOpt = trainerRepository.findByUsername(username);
            if (!trainerOpt.isPresent()) {
                logger.warn("Trainer not found for username: {}", username);
                return false;
            }

            Trainer trainer = trainerOpt.get();

            // Check if already in desired state (non-idempotent check)
            if (trainer.getUser().getIsActive() == active) {
                logger.warn("Trainer is already {}", active ? "active" : "inactive");
                return false;
            }

            trainer.getUser().setIsActive(active);
            userRepository.save(trainer.getUser());

            logger.info("Successfully {} trainer with username: {}",
                    active ? "activated" : "deactivated", username);
            return true;

        } catch (Exception e) {
            logger.error("Error {} trainer with username: {}",
                    active ? "activating" : "deactivating", username, e);
            throw new RuntimeException("Failed to update trainer status", e);
        }
    }

    @Transactional
    public boolean deleteTrainerProfile(String username, String password) {
        logger.info("Deleting trainer profile for username: {}", username);

        try {
            if (!authenticationService.authenticateTrainer(username, password)) {
                logger.warn("Authentication failed for trainer username: {}", username);
                return false;
            }

            Optional<Trainer> trainerOpt = trainerRepository.findByUsername(username);
            if (!trainerOpt.isPresent()) {
                logger.warn("Trainer not found for username: {}", username);
                return false;
            }

            Trainer trainer = trainerOpt.get();
            trainerRepository.delete(trainer);

            logger.info("Successfully deleted trainer profile for username: {}", username);
            return true;

        } catch (Exception e) {
            logger.error("Error deleting trainer profile for username: {}", username, e);
            throw new RuntimeException("Failed to delete trainer profile", e);
        }
    }

    public List<Training> getTrainerTrainingsByCriteria(
            String trainerUsername,
            String password,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName,
            String trainingTypeName
    ) {
        logger.info("Fetching trainings for trainee [{}] with criteria: fromDate={}, toDate={}, trainerName={}, trainingTypeName={}",
                trainerUsername, fromDate, toDate, traineeName, trainingTypeName);

        try {
            if (!authenticationService.authenticateTrainee(trainerUsername, password)) {
                logger.warn("Authentication failed for trainee: {}", trainerUsername);
                return Collections.emptyList();
            }

            Optional<Trainer> trainerOpt = trainerRepository.findByUsername(trainerUsername);
            if (!trainerOpt.isPresent()) {
                logger.warn("Trainee not found for username: {}", trainerUsername);
                return Collections.emptyList();
            }

            List<Training> trainings = trainerRepository.findTrainingsByTrainerUsernameWithCriteria(
                    trainerUsername, fromDate, toDate, traineeName, trainingTypeName);

            if (trainings == null || trainings.isEmpty()) {
                logger.info("No trainings found for trainee [{}] with given criteria", trainerUsername);
                return Collections.emptyList();
            }

            logger.info("Found {} trainings for trainee [{}]", trainings.size(), trainerUsername);
            return trainings;

        } catch (Exception e) {
            logger.error("Error while fetching trainings for trainee [{}]", trainerUsername, e);
            throw new RuntimeException("Failed to fetch trainings", e);
        }
    }
}
