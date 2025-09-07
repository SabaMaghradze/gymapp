package com.rest.gymapp.service.impl;

import com.rest.gymapp.dto.request.trainee.TraineeUpdateRequest;
import com.rest.gymapp.dto.response.RegistrationResponse;
import com.rest.gymapp.dto.response.trainee.TraineeProfileResponse;
import com.rest.gymapp.dto.response.trainee.TraineeResponse;
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
import jakarta.persistence.PersistenceException;
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

    public TraineeResponse updateTraineeProfile(TraineeUpdateRequest req, String password) {

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

        return mappers.getTraineeResponse(updatedTrainee);
    }

    public void deleteTraineeProfile(String username, String password) {

        logger.info("Deleting trainee profile for username: {}", username);

        authenticationService.authenticateTrainee(username, password);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found"));

        traineeRepository.delete(trainee);

        logger.info("Successfully deleted trainee profile and associated trainings for username: {}", username);
    }

    public List<Trainer> findNonAssignedTrainers(String traineeUsername, String password) {
        logger.info("Searching for all the trainers that are not assigned to this specific trainee: {}", traineeUsername);

        try {
            if (!authenticationService.authenticateTrainee(traineeUsername, password)) {
                logger.warn("Authentication failed for trainee username: {}", traineeUsername);
                return Collections.emptyList();
            }

            Optional<Trainee> traineeOpt = traineeRepository.findByUsername(traineeUsername);
            if (!traineeOpt.isPresent()) {
                logger.warn("Trainee not found for username: {}", traineeUsername);
                return Collections.emptyList();
            }

            List<Trainer> trainers = traineeRepository.findTrainersNotAssignedToTrainee(traineeUsername);

            if (trainers == null || trainers.isEmpty()) {
                logger.warn("No trainers found: {}", traineeUsername);
                Collections.emptyList();
            }

            logger.info("Successfully fetched all the trainers: {}", traineeUsername);

            return trainers;

        } catch (PersistenceException e) {
            logger.error("Failed to fetch trainers: {}", traineeUsername, e);
            throw new PersistenceException(e);
        }
    }

    public List<Training> getTraineeTrainingsByCriteria(
            String traineeUsername,
            String password,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            String trainingTypeName
    ) {
        logger.info("Fetching trainings for trainee [{}] with criteria: fromDate={}, toDate={}, trainerName={}, trainingTypeName={}",
                traineeUsername, fromDate, toDate, trainerName, trainingTypeName);

        try {
            if (!authenticationService.authenticateTrainee(traineeUsername, password)) {
                logger.warn("Authentication failed for trainee: {}", traineeUsername);
                return Collections.emptyList();
            }

            Optional<Trainee> traineeOpt = traineeRepository.findByUsername(traineeUsername);
            if (!traineeOpt.isPresent()) {
                logger.warn("Trainee not found for username: {}", traineeUsername);
                return Collections.emptyList();
            }

            List<Training> trainings = traineeRepository.findTrainingsByTraineeUsernameWithCriteria(
                    traineeUsername, fromDate, toDate, trainerName, trainingTypeName);

            if (trainings == null || trainings.isEmpty()) {
                logger.info("No trainings found for trainee [{}] with given criteria", traineeUsername);
                return Collections.emptyList();
            }

            logger.info("Found {} trainings for trainee [{}]", trainings.size(), traineeUsername);
            return trainings;

        } catch (Exception e) {
            logger.error("Error while fetching trainings for trainee [{}]", traineeUsername, e);
            throw new RuntimeException("Failed to fetch trainings", e);
        }
    }
}
