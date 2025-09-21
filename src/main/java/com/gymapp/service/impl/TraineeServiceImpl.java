package com.gymapp.service.impl;

import com.gymapp.dto.request.trainee.TraineeActivationRequest;
import com.gymapp.dto.request.trainee.TraineeRegistrationRequest;
import com.gymapp.dto.request.trainee.TraineeUpdateRequest;
import com.gymapp.dto.request.trainee.UpdateTraineeTrainersRequest;
import com.gymapp.dto.response.RegistrationResponse;
import com.gymapp.dto.response.trainee.TraineeProfileResponse;
import com.gymapp.dto.response.trainee.TraineeUpdateResponse;
import com.gymapp.dto.response.trainer.TrainerResponseBasic;
import com.gymapp.dto.response.training.TrainingResponseForTrainee;
import com.gymapp.exception.ResourceNotFoundException;
import com.gymapp.exception.UserNotFoundException;
import com.gymapp.model.Trainee;
import com.gymapp.model.Trainer;
import com.gymapp.model.Training;
import com.gymapp.model.User;
import com.gymapp.monitoring.metrics.TraineeMetrics;
import com.gymapp.repository.TraineeRepository;
import com.gymapp.repository.TrainerRepository;
import com.gymapp.repository.UserRepository;
import com.gymapp.service.AuthenticationService;
import com.gymapp.service.TraineeService;
import com.gymapp.utils.CredentialsGenerator;
import com.gymapp.utils.Mappers;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class TraineeServiceImpl implements TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final CredentialsGenerator credentialsGenerator;
    private final Mappers mappers;
    private final TraineeMetrics traineeMetrics;

    @Transactional
    public RegistrationResponse createTraineeProfile(TraineeRegistrationRequest req, String transactionId) {

        logger.info("Registering trainee profile for: {} {}, transaction id: {}", req.firstName(), req.lastName(), transactionId);

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

        traineeMetrics.incrementTraineesCreated();

        RegistrationResponse response = new RegistrationResponse(username, password);
        logger.info("[{}] Successfully created trainee profile: {}", transactionId, response);

        return response;
    }

    public TraineeProfileResponse getTraineeProfileByUsername(String username, String password, String transactionId) {

        logger.info("[{}] Getting trainee profile for username: {}", transactionId, username);

        authenticationService.authenticateTrainee(username, password);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found"));

        TraineeProfileResponse response = mappers.getTraineeProfileResponse(trainee);
        logger.info("[{}] Fetched trainee profile: {}", transactionId, response);

        return response;
    }

    // also assuming that trainer can change the status of a trainee instead of admin,
    // since we are not implementing admin functionality.
    @Transactional
    public void activateDeactivateTrainee(TraineeActivationRequest req, String username, String password, String transactionId) {

        logger.info("{} trainer with username: {}", req.isActive() ? "Activating" : "Deactivating", username);

        authenticationService.authenticateTrainer(username, password);

        Trainee trainee = traineeRepository.findByUserUsername(req.username())
                .orElseThrow(() -> new UserNotFoundException("Trainee not found"));

        if (trainee.getUser().getIsActive() == req.isActive()) {
            logger.warn("Trainer is already {}", req.isActive() ? "active" : "inactive");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already " + (req.isActive() ? "active" : "inactive"));
        }

        trainee.getUser().setIsActive(req.isActive());
        userRepository.save(trainee.getUser());

        logger.info("[{}] Successfully {} trainee with username: {}", transactionId, req.isActive() ? "activated" : "deactivated", req.username());
    }

    @Transactional
    public TraineeUpdateResponse updateTraineeProfile(TraineeUpdateRequest req, String username, String password, String transactionId) {

        logger.info("[{}] Updating trainee profile for username: {}", transactionId, username);

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

        TraineeUpdateResponse response = mappers.getTraineeUpdateResponse(updatedTrainee);
        logger.info("[{}] Successfully updated trainee profile: {}", transactionId, response);

        return response;
    }

    @Transactional
    public void deleteTraineeProfile(String username, String password, String transactionId) {

        logger.info("[{}] Deleting trainee profile for username: {}", transactionId, username);

        authenticationService.authenticateTrainee(username, password);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found"));

        traineeRepository.delete(trainee);

        logger.info("[{}] Successfully deleted trainee profile and associated trainings for username: {}", transactionId, username);
    }

    public List<TrainerResponseBasic> findNonAssignedTrainers(String traineeUsername, String password, String transactionId) {

        logger.info("[{}] Searching for all trainers not assigned to trainee: {}", transactionId, traineeUsername);

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
            logger.info("[{}] Non-assigned trainers for trainee {}: {}", transactionId, traineeUsername, responses);
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

        logger.info("[{}] Successfully fetched non-assigned trainers: {}", transactionId, responses);

        return responses;
    }

    public List<TrainingResponseForTrainee> findTraineeTrainings(String username,
                                                                 String password,
                                                                 LocalDate fromDate,
                                                                 LocalDate toDate,
                                                                 String trainerName,
                                                                 String trainingType,
                                                                 String transactionId) {

        logger.info("[{}] Fetching trainings for trainee: {}", transactionId, username);

        authenticationService.authenticateTrainee(username, password);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Set<Training> trainings = trainee.getTrainings();

        if (trainings == null || trainings.isEmpty()) {
            logger.info("[{}] No trainings found for trainee [{}]", transactionId, username);
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

        List<Training> resultList = filtered.toList();

        if (resultList.isEmpty()) {
            logger.info("[{}] No trainings found for trainee [{}] with given criteria", transactionId, username);
            throw new ResourceNotFoundException("Failed to fetch trainings with the given criteria");
        }

        List<TrainingResponseForTrainee> response = resultList.stream()
                .map(mappers::getTrainingResponseForTrainee)
                .toList();

        logger.info("[{}] Found {} trainings for trainee [{}]: {}", transactionId, response.size(), username, response);

        return response;
    }

    // this task was not specified in the description, so I considered the trainers
    // with usernames provided in the request should be added to trainers list, if a
    // name is missing, it will be ignored (not removed).
    @Override
    @Transactional
    public List<TrainerResponseBasic> updateTraineeTrainers(UpdateTraineeTrainersRequest req,
                                                            String username,
                                                            String password,
                                                            String transactionId) {

        logger.info("[{}] Fetching trainers of trainee: {}", transactionId, username);

        authenticationService.authenticateTrainee(username, password);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found"));

        Set<Trainer> existingTrainers = trainee.getTrainers();

        Set<String> existingTrainersUsernames = existingTrainers.stream()
                .map(trainer -> trainer.getUser().getUsername())
                .collect(Collectors.toSet());

        List<Trainer> trainersToAdd = req.getTrainers().stream()
                .map(trainerReq -> trainerRepository.findByUserUsername(trainerReq.getUsername())
                        .orElseThrow(() -> new UserNotFoundException("Trainer not found: " + trainerReq.getUsername())))
                .filter(trainer -> !existingTrainersUsernames.contains(trainer.getUser().getUsername()))
                .toList();

        if (existingTrainers.isEmpty()) {
            throw new ResourceNotFoundException("Failed to find trainers for this trainee");
        }

        existingTrainers.addAll(trainersToAdd);
        trainee.setTrainers(existingTrainers);
        traineeRepository.save(trainee);

        List<TrainerResponseBasic> response = existingTrainers.stream()
                .map(mappers::getTrainerResponseBasic)
                .toList();

        logger.info("[{}] Trainers fetched for trainee {}: {}", transactionId, username, response);

        return response;
    }
}
