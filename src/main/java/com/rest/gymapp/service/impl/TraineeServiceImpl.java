package com.rest.gymapp.service.impl;

import com.rest.gymapp.dto.request.trainee.TraineeActivationRequest;
import com.rest.gymapp.dto.request.trainee.TraineeRegistrationRequest;
import com.rest.gymapp.dto.request.trainee.TraineeUpdateRequest;
import com.rest.gymapp.dto.request.trainee.UpdateTraineeTrainersRequest;
import com.rest.gymapp.dto.request.training.TraineeTrainingsRequest;
import com.rest.gymapp.dto.response.RegistrationResponse;
import com.rest.gymapp.dto.response.trainee.TraineeProfileResponse;
import com.rest.gymapp.dto.response.trainee.TraineeUpdateResponse;
import com.rest.gymapp.dto.response.trainer.TrainerResponseBasic;
import com.rest.gymapp.dto.response.training.TrainingResponseForTrainee;
import com.rest.gymapp.exception.ResourceNotFoundException;
import com.rest.gymapp.exception.UserNotFoundException;
import com.rest.gymapp.model.Trainee;
import com.rest.gymapp.model.Trainer;
import com.rest.gymapp.model.Training;
import com.rest.gymapp.model.User;
import com.rest.gymapp.repository.TraineeRepository;
import com.rest.gymapp.repository.TrainerRepository;
import com.rest.gymapp.repository.UserRepository;
import com.rest.gymapp.service.AuthenticationService;
import com.rest.gymapp.service.TraineeService;
import com.rest.gymapp.utils.CredentialsGenerator;
import com.rest.gymapp.utils.Mappers;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class TraineeServiceImpl implements TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final CredentialsGenerator credentialsGenerator;
    private final Mappers mappers;

    public RegistrationResponse createTraineeProfile(TraineeRegistrationRequest req) {

        logger.info("Registering trainee profile for: {} {}", req.firstName(), req.lastName());

        String username = credentialsGenerator.generateUsername(req.firstName(), req.lastName(), userRepository);
        String password = credentialsGenerator.generatePassword();

        User user = new User();
        user.setFirstName(req.firstName().trim());
        user.setLastName(req.lastName().trim());
        user.setUsername(username);
        user.setPassword(password);
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        Trainee trainee = new Trainee();

        if (req.dateOfBirth() != null) {
            trainee.setDateOfBirth(req.dateOfBirth());
        }

        if (req.address() != null) {
            trainee.setAddress(req.address());
        }

        trainee.setUser(savedUser);

        traineeRepository.save(trainee);

        logger.info("Successfully created trainee profile for username: {}", username);
        return new RegistrationResponse(username, password);
    }

    public TraineeProfileResponse getTraineeProfileByUsername(String username, String password) {

        logger.info("Getting trainee profile for username: {}", username);

        authenticationService.authenticateTrainee(username, password);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found"));

        return mappers.getTraineeProfileResponse(trainee);
    }

    public void activateDeactivateTrainee(TraineeActivationRequest req, String password) {

        logger.info("{} trainer with username: {}", req.isActive() ? "Activating" : "Deactivating", req.username());

        authenticationService.authenticateTrainer(req.username(), password);

        Trainee trainee = traineeRepository.findByUserUsername(req.username())
                .orElseThrow(() -> new UserNotFoundException("Trainee not found"));

        if (trainee.getUser().getIsActive() == req.isActive()) {
            logger.warn("Trainer is already {}", req.isActive() ? "active" : "inactive");
            return;
        }

        trainee.getUser().setIsActive(req.isActive());
        userRepository.save(trainee.getUser());

        logger.info("Successfully {} trainer with username: {}",
                req.isActive() ? "activated" : "deactivated", req.username());

    }

    public TraineeUpdateResponse updateTraineeProfile(TraineeUpdateRequest req, String username, String password) {

        logger.info("Updating trainer profile for username: {}", username);

        authenticationService.authenticateTrainee(username, password);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found"));

        User user = trainee.getUser();

        if (!req.firstName().equals(user.getFirstName()) || !req.lastName().equals(user.getLastName())) {
            String newUserName = credentialsGenerator.generateUsername(req.firstName(), req.lastName(), userRepository);
            user.setUsername(newUserName);
        }

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

        logger.info("Successfully updated trainer profile for username: {}", username);

        return mappers.getTraineeUpdateResponse(updatedTrainee);
    }

    public void deleteTraineeProfile(String username, String password) {

        logger.info("Deleting trainee profile for username: {}", username);

        authenticationService.authenticateTrainee(username, password);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found"));

        traineeRepository.delete(trainee);

        logger.info("Successfully deleted trainee profile and associated trainings for username: {}", username);
    }

    @Transactional(readOnly = true)
    public List<TrainerResponseBasic> findNonAssignedTrainers(String traineeUsername, String password) {

        logger.info("Searching for all the trainers that are not assigned to trainee: {}", traineeUsername);

        authenticationService.authenticateTrainee(traineeUsername, password);

        List<TrainerResponseBasic> responses = new ArrayList<>();

        Trainee trainee = traineeRepository.findByUserUsername(traineeUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<Trainer> allTrainers = trainerRepository.findAll();
        if (allTrainers.isEmpty()) {
            throw new ResourceNotFoundException("Failed to fetch all trainers");
        }

        Set<Trainer> traineeTrainers = trainee.getTrainers();
        if (traineeTrainers.isEmpty()) {
            for (Trainer trainer : allTrainers) {
                responses.add(mappers.getTrainerResponseBasic(trainer));
            }
            return responses;
        }

        List<Trainer> nonAssignedTrainers = allTrainers.stream()
                .filter(trainer -> trainer.getTrainees().stream()
                        .noneMatch(t -> t.getUser().getUsername().equals(traineeUsername)))
                .toList();

        if (nonAssignedTrainers.isEmpty()) {
            throw new ResourceNotFoundException("All trainers are already assigned to this trainee");
        }

        for (Trainer trainer : nonAssignedTrainers) {
            responses.add(mappers.getTrainerResponseBasic(trainer));
        }

        logger.info("Successfully fetched all the trainers: {}", nonAssignedTrainers);

        return responses;
    }

    public List<TrainingResponseForTrainee> findTraineeTrainings(String username,
                                                                 String password,
                                                                 LocalDate fromDate,
                                                                 LocalDate toDate,
                                                                 String trainerName,
                                                                 String trainingType) {

        logger.info("Fetching trainings for trainee: {}", username);

        authenticationService.authenticateTrainee(username, password);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Set<Training> trainings = trainee.getTrainings();

        if (trainings == null || trainings.isEmpty()) {
            logger.info("No trainings found for trainee [{}] with given criteria", username);
            throw new ResourceNotFoundException("Failed to find any trainings.");
        }

        Stream<Training> filtered = trainings.stream()
                .filter(tr -> fromDate == null || !tr.getTrainingDate().isBefore(fromDate))
                .filter(tr -> toDate == null || !tr.getTrainingDate().isAfter(toDate))
                .filter(tr -> {
                    if (trainerName == null) return true;

                    String tn = trainerName.toLowerCase();
                    String first = tr.getTrainer().getUser().getFirstName().toLowerCase();
                    String last = tr.getTrainer().getUser().getLastName().toLowerCase();
                    String full = first + " " + last;

                    return first.contains(tn) || last.contains(tn) || full.contains(tn);
                })
                .filter(tr -> trainingType == null ||
                        tr.getTrainingType().getTrainingTypeName().equalsIgnoreCase(trainingType));

        List<Training> resultList = filtered.collect(Collectors.toUnmodifiableList());

        if (resultList.isEmpty()) {
            logger.info("No trainings found for trainee [{}] with given criteria", username);
            throw new ResourceNotFoundException("Failed to fetch trainings with the given criteria");
        }

        logger.info("Found {} trainings for trainee [{}]", trainings.size(), username);

        return resultList.stream()
                .map(mappers::getTrainingResponseForTrainee)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<TrainerResponseBasic> updateTraineeTrainers(UpdateTraineeTrainersRequest req, String username, String password) {

        logger.info("Fetching trainers of trainee: {}", username);

        authenticationService.authenticateTrainee(username, password);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found"));

        Set<Trainer> existingTrainers = trainee.getTrainers();

        if (existingTrainers.isEmpty()) {
            throw new ResourceNotFoundException("Failed to find trainers for this trainee");
        }

        return existingTrainers.stream()
                .map(mappers::getTrainerResponseBasic)
                .collect(Collectors.toUnmodifiableList());
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
}
