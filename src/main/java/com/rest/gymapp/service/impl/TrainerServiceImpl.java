package com.rest.gymapp.service.impl;

import com.rest.gymapp.dto.request.auth.LoginRequest;
import com.rest.gymapp.dto.request.trainer.TrainerActivationRequest;
import com.rest.gymapp.dto.request.trainer.TrainerRegistrationRequest;
import com.rest.gymapp.dto.request.trainer.TrainerUpdateRequest;
import com.rest.gymapp.dto.request.training.TrainerTrainingsRequest;
import com.rest.gymapp.dto.response.RegistrationResponse;
import com.rest.gymapp.dto.response.trainer.TrainerProfileResponse;
import com.rest.gymapp.dto.response.trainer.TrainerUpdateResponse;
import com.rest.gymapp.dto.response.training.TrainingResponseForTrainer;
import com.rest.gymapp.exception.ResourceNotFoundException;
import com.rest.gymapp.exception.UserNotFoundException;
import com.rest.gymapp.model.*;
import com.rest.gymapp.repository.TrainerRepository;
import com.rest.gymapp.repository.TrainingTypeRepository;
import com.rest.gymapp.repository.UserRepository;
import com.rest.gymapp.service.AuthenticationService;
import com.rest.gymapp.service.TrainerService;
import com.rest.gymapp.utils.CredentialsGenerator;
import com.rest.gymapp.utils.Mappers;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);

    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final CredentialsGenerator credentialsGenerator;
    private final Mappers mappers;
    private final TrainingTypeRepository trainingTypeRepository;

    public RegistrationResponse createTrainerProfile(TrainerRegistrationRequest req) {

        logger.info("Creating trainer profile for: {} {}", req.firstName(), req.lastName());

        TrainingType trainingType = trainingTypeRepository.findByTrainingTypeName(req.specializationName())
                .orElseThrow(() -> new ResourceNotFoundException("Training type not found"));

        String username = credentialsGenerator.generateUsername(req.firstName(), req.lastName(), userRepository);
        String password = credentialsGenerator.generatePassword();

        User user = new User();
        user.setFirstName(req.firstName().trim());
        user.setLastName(req.lastName().trim());
        user.setUsername(username);
        user.setPassword(password);
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        Trainer trainer = new Trainer();

        trainer.setSpecialization(trainingType);
        trainer.setUser(savedUser);

        Trainer savedTrainer = trainerRepository.save(trainer);

        logger.info("Successfully created trainer profile for username: {}", username);
        return new RegistrationResponse(username, password);
    }

    public TrainerProfileResponse getTrainerByUsername(String username, String password) {

        logger.info("Getting trainer profile for username: {}", username);

        authenticationService.authenticateTrainer(username, password);

        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainer not found"));

        return mappers.getTrainerProfileResponse(trainer);
    }

    public TrainerUpdateResponse updateTrainerProfile(TrainerUpdateRequest req, String username, String password) {

        logger.info("Updating trainer profile for username: {}", username);

        authenticationService.authenticateTrainer(username, password);

        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainer not found"));

        User user = trainer.getUser();

        if (!req.firstName().equals(user.getFirstName()) || !req.lastName().equals(user.getLastName())) {
            String newUserName = credentialsGenerator.generateUsername(req.firstName(), req.lastName(), userRepository);
            user.setUsername(newUserName);
        }

        user.setFirstName(req.firstName().trim());
        user.setLastName(req.lastName().trim());
        user.setIsActive(req.isActive());

        if (req.trainingType() != null) {
            TrainingType trainingType = trainingTypeRepository.findByTrainingTypeName(req.trainingType().getTrainingTypeName())
                    .orElseThrow(() -> new ResourceNotFoundException("No training type found"));
            trainer.setSpecialization(trainingType);
        }

        userRepository.save(user);
        Trainer updatedTrainer = trainerRepository.save(trainer);

        logger.info("Successfully updated trainer profile for username: {}", username);
        return mappers.getTrainerUpdateResponse(updatedTrainer);
    }

    public void activateDeactivateTrainer(TrainerActivationRequest req, String password) {

        logger.info("{} trainer with username: {}", req.isActive() ? "Activating" : "Deactivating", req.username());

        authenticationService.authenticateTrainer(req.username(), password);

        Trainer trainer = trainerRepository.findByUserUsername(req.username())
                .orElseThrow(() -> new UserNotFoundException("Trainer not found"));

        if (trainer.getUser().getIsActive() == req.isActive()) {
            logger.warn("Trainer is already {}", req.isActive() ? "active" : "inactive");
            return;
        }

        trainer.getUser().setIsActive(req.isActive());
        userRepository.save(trainer.getUser());

        logger.info("Successfully {} trainer with username: {}", req.isActive() ? "activated" : "deactivated", req.username());
    }

    public List<TrainingResponseForTrainer> findTrainerTrainingsByCriteria(String username,
                                                                           String password,
                                                                           LocalDate fromDate,
                                                                           LocalDate toDate,
                                                                           String traineeName) {

        logger.info("Fetching trainings for trainer: {}", username);

        authenticationService.authenticateTrainer(username, password);

        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Failed to find trainer"));

        Set<Training> trainings = trainer.getTrainings();

        if (trainings == null || trainings.isEmpty()) {
            logger.info("No trainings found for trainer [{}] with given criteria", username);
            throw new ResourceNotFoundException("Failed to find any trainings.");
        }

        Stream<Training> filtered = trainings.stream()
                .filter(tr -> fromDate == null || !tr.getTrainingDate().isBefore(fromDate))
                .filter(tr -> toDate == null || !tr.getTrainingDate().isAfter(toDate))
                .filter(tr -> {
                    if (traineeName == null) return true;

                    String tn = traineeName.toLowerCase();
                    String first = tr.getTrainee().getUser().getFirstName().toLowerCase();
                    String last = tr.getTrainee().getUser().getLastName().toLowerCase();
                    String full = first + " " + last;

                    return first.contains(tn) || last.contains(tn) || full.contains(tn);
                });

        List<Training> resultList = filtered.collect(Collectors.toUnmodifiableList());

        if (resultList.isEmpty()) {
            logger.info("No trainings found for trainer [{}] with given criteria", username);
            throw new ResourceNotFoundException("Failed to fetch trainings with the given criteria");
        }

        logger.info("Found {} trainings for trainee [{}]", trainings.size(), username);

        return resultList.stream()
                .map(mappers::getTrainingResponseForTrainer)
                .collect(Collectors.toUnmodifiableList());
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
}
