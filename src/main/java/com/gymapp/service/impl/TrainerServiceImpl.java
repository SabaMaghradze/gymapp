package com.gymapp.service.impl;

import com.gymapp.dto.request.trainer.TrainerActivationRequest;
import com.gymapp.dto.request.trainer.TrainerRegistrationRequest;
import com.gymapp.dto.request.trainer.TrainerUpdateRequest;
import com.gymapp.dto.response.RegistrationResponse;
import com.gymapp.dto.response.trainer.TrainerProfileResponse;
import com.gymapp.dto.response.trainer.TrainerUpdateResponse;
import com.gymapp.dto.response.training.TrainingResponseForTrainer;
import com.gymapp.exception.resource.ResourceNotFoundException;
import com.gymapp.exception.role.RoleNotFoundException;
import com.gymapp.exception.user.UserNotFoundException;
import com.gymapp.model.*;
import com.gymapp.monitoring.metrics.TrainerMetrics;
import com.gymapp.repository.RoleRepository;
import com.gymapp.repository.TrainerRepository;
import com.gymapp.repository.TrainingTypeRepository;
import com.gymapp.repository.UserRepository;
import com.gymapp.service.TrainerService;
import com.gymapp.utils.CredentialsGenerator;
import com.gymapp.utils.Mappers;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerServiceImpl.class);
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final CredentialsGenerator credentialsGenerator;
    private final Mappers mappers;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainerMetrics trainerMetrics;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RegistrationResponse createTrainerProfile(TrainerRegistrationRequest req, String transactionId) {

        logger.info("[{}] Creating trainer profile for: {} {}", transactionId, req.firstName(), req.lastName());

        TrainingType trainingType = trainingTypeRepository.findByTrainingTypeName(req.specializationName())
                .orElseThrow(() -> new ResourceNotFoundException("Training type not found"));

        String username = credentialsGenerator.generateUsername(req.firstName(), req.lastName(), userRepository);
        String password = req.password();

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RoleNotFoundException("Role ROLE_USER not found."));

        User user = new User();
        user.setFirstName(req.firstName().trim());
        user.setLastName(req.lastName().trim());
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setIsActive(true);
        user.setIsEnabled(true);
        user.setAccNonLocked(true);
        user.setNumberOfFailedAttempts(0);

        user.setRoles(Collections.singletonList(userRole));

        User savedUser = userRepository.save(user);

        Trainer trainer = new Trainer();

        trainer.setSpecialization(trainingType);
        trainer.setUser(savedUser);

        trainerRepository.save(trainer);

        trainerMetrics.incrementTrainersCreated();

        RegistrationResponse response = new RegistrationResponse(username, password);
        logger.info("[{}] Successfully created trainer profile: {}", transactionId, response);
        return response;
    }

    public List<Trainer> getAllTrainers() {

        List<Trainer> allTrainers = trainerRepository.findAll();

        if (allTrainers.isEmpty()) {
            throw new ResourceNotFoundException("No trainers found");
        }

        return allTrainers;
    }

    public TrainerProfileResponse getTrainerById(Long id, String transactionId) {

        logger.info("[{}] Getting trainer profile for username={}", transactionId, id);

        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Trainer not found"));

        TrainerProfileResponse response = mappers.getTrainerProfileResponse(trainer);
        logger.info("[{}] Successfully fetched trainer profile for username={}", transactionId, id);
        return response;
    }

    @Transactional
    public TrainerUpdateResponse updateTrainerProfile(Long id, TrainerUpdateRequest req, String transactionId) {

        logger.info("[{}] Updating trainer profile for trainer={}, payload={}", transactionId, id, req);

        Trainer trainer = trainerRepository.findById(id)
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

        TrainerUpdateResponse response = mappers.getTrainerUpdateResponse(updatedTrainer);
        logger.info("[{}] Successfully updated trainer profile for username={}", transactionId, user.getUsername());
        return response;
    }

    // we are assuming that trainers can change each other's statuses, since we don't have
    // admin functionality set up yet.
    @Transactional
    public void activateDeactivateTrainer(Long id, TrainerActivationRequest req, String transactionId) {

        logger.info("[{}] {} trainer with username={}", transactionId,
                req.isActive() ? "Activating" : "Deactivating", req.username());

        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Trainer not found"));

        if (trainer.getUser().getIsActive() == req.isActive()) {
            logger.warn("Trainer is already {}", req.isActive() ? "active" : "inactive");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already " + (req.isActive() ? "active" : "inactive"));
        }

        trainer.getUser().setIsActive(req.isActive());
        userRepository.save(trainer.getUser());

        logger.info("[{}] Successfully {} trainer username={}", transactionId,
                req.isActive() ? "activated" : "deactivated", req.username());
    }

    public List<TrainingResponseForTrainer> findTrainerTrainingsByCriteria(Long id,
                                                                           LocalDate fromDate,
                                                                           LocalDate toDate,
                                                                           String traineeName,
                                                                           String transactionId) {

        logger.info("[{}] Fetching trainings for trainer={}, fromDate={}, toDate={}, traineeName={}",
                transactionId, id, fromDate, toDate, traineeName);

        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Failed to find trainer"));

        Set<Training> trainings = trainer.getTrainings();

        if (trainings == null || trainings.isEmpty()) {
            logger.info("There are no trainings registered by trainer with id: {}", id);
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

        List<Training> resultList = filtered.toList();

        if (resultList.isEmpty()) {
            logger.info("[{}] No trainings found for trainer={} with given criteria", transactionId, id);
            throw new ResourceNotFoundException("Failed to fetch trainings with the given criteria");
        }

        logger.info("[{}] Found {} trainings for trainer={}", transactionId, resultList.size(), id);

        return resultList.stream()
                .map(mappers::getTrainingResponseForTrainer)
                .toList();
    }
}
