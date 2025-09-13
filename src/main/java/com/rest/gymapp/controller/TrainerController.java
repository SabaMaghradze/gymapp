package com.rest.gymapp.controller;

import com.rest.gymapp.dto.request.trainer.TrainerActivationRequest;
import com.rest.gymapp.dto.request.trainer.TrainerRegistrationRequest;
import com.rest.gymapp.dto.request.trainer.TrainerUpdateRequest;
import com.rest.gymapp.dto.response.RegistrationResponse;
import com.rest.gymapp.dto.response.trainer.TrainerProfileResponse;
import com.rest.gymapp.dto.response.trainer.TrainerUpdateResponse;
import com.rest.gymapp.dto.response.training.TrainingResponseForTrainer;
import com.rest.gymapp.service.TrainerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trainers")
public class TrainerController {

    private final TrainerService trainerService;
    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);

    // done
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerTrainer(
            @Valid @RequestBody TrainerRegistrationRequest req) {

        String transactionId = UUID.randomUUID().toString();
        logger.info("[{}] POST /api/trainers/register called with payload: {}", transactionId, req);

        RegistrationResponse response = trainerService.createTrainerProfile(req, transactionId);

        logger.info("[{}] Trainer registered successfully: {}", transactionId, response.username());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // done
    // test this again after adding trainees to make sure there is trainee responses in the dto.
    @GetMapping("/trainer")
    public ResponseEntity<TrainerProfileResponse> getTrainer(@RequestHeader String username,
                                                             @RequestHeader String password) {

        String transactionId = UUID.randomUUID().toString();
        logger.info("[{}] GET /api/trainers/trainer called for user: {}", transactionId, username);

        TrainerProfileResponse response = trainerService.getTrainerByUsername(username, password, transactionId);

        logger.info("[{}] Trainer profile retrieved successfully for username={}", transactionId, username);
        return ResponseEntity.ok(response);
    }

    // may change dto, since for now username is not required in the request,
    // trainer cannot change it themselves
    @PutMapping("/update-trainer")
    public ResponseEntity<TrainerUpdateResponse> updateTrainer(@Valid @RequestBody TrainerUpdateRequest req,
                                                               @RequestHeader String username,
                                                               @RequestHeader String password) {

        String transactionId = UUID.randomUUID().toString();
        logger.info("[{}] PUT /api/trainers/update-trainer called for username={} with payload={}", transactionId, username, req);

        TrainerUpdateResponse response = trainerService.updateTrainerProfile(req, username, password, transactionId);

        logger.info("[{}] Trainer profile updated successfully for username={}", transactionId, username);
        return ResponseEntity.ok(response);

    }

    // to fix (most likely issue -> lazy-loading)
    @GetMapping("/trainer/trainings")
    public ResponseEntity<List<TrainingResponseForTrainer>> findTrainerTrainings(
            @RequestHeader String username,
            @RequestHeader String password,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) String traineeName
    ) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("[{}] GET /api/trainers/trainer/trainings called for username={}, fromDate={}, toDate={}, traineeName={}",
                transactionId, username, fromDate, toDate, traineeName);

        List<TrainingResponseForTrainer> response =
                trainerService.findTrainerTrainingsByCriteria(username, password, fromDate, toDate, traineeName, transactionId);

        logger.info("[{}] Found {} trainings for trainer={}", transactionId, response.size(), username);
        return ResponseEntity.ok(response);
    }

    // to test
    @PatchMapping("/trainer/activate-deactivate")
    public ResponseEntity<?> changeActivationStatus(@Valid @RequestBody TrainerActivationRequest req,
                                                    @RequestHeader String username,
                                                    @RequestHeader String password) {

        String transactionId = UUID.randomUUID().toString();
        logger.info("[{}] PATCH /api/trainers/trainer/activate-deactivate called: {}", transactionId, req);

        trainerService.activateDeactivateTrainer(req, username, password, transactionId);

        logger.info("[{}] PATCH /api/trainers/trainer/activate-deactivate response: success", transactionId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
