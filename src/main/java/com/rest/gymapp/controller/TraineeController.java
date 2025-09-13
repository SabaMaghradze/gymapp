package com.rest.gymapp.controller;

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
import com.rest.gymapp.service.TraineeService;
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
@RequestMapping("/api/trainees")
public class TraineeController {

    private final TraineeService traineeService;
    private static final Logger logger = LoggerFactory.getLogger(TraineeController.class);

    //done
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerTrainee(
            @Valid @RequestBody TraineeRegistrationRequest req) {

        String transactionId = UUID.randomUUID().toString();
        logger.info("[{}] POST /api/trainees/register called: {}", transactionId, req);

        RegistrationResponse response = traineeService.createTraineeProfile(req, transactionId);

        logger.info("[{}] POST /api/trainees/register response: {}", transactionId, response);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // done
    @GetMapping("/trainee")
    public ResponseEntity<TraineeProfileResponse> getTrainee(@RequestHeader String username,
                                                             @RequestHeader String password) {

        String transactionId = UUID.randomUUID().toString();
        logger.info("[{}] GET /api/trainees/trainee called: {}", transactionId, username);

        TraineeProfileResponse response = traineeService.getTraineeProfileByUsername(username, password, transactionId);

        logger.info("[{}] GET /api/trainees/trainee response: {}", transactionId, response);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // done
    @PutMapping("/update-trainee")
    public ResponseEntity<TraineeUpdateResponse> updateTrainee(@Valid @RequestBody TraineeUpdateRequest req,
                                                               @RequestHeader String username,
                                                               @RequestHeader String password) {

        String transactionId = UUID.randomUUID().toString();
        logger.info("[{}] PUT /api/trainees/update-trainee called: {}", transactionId, username);

        TraineeUpdateResponse response = traineeService.updateTraineeProfile(req, username, password, transactionId);

        logger.info("[{}] PUT /api/trainees/update-trainee response: {}", transactionId, response);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // done
    @DeleteMapping("/delete-trainee")
    public ResponseEntity<?> deleteTrainee(@RequestHeader String username,
                                           @RequestHeader String password) {

        String transactionId = UUID.randomUUID().toString();
        logger.info("[{}] DELETE /api/trainees/delete-trainee called: {}", transactionId, username);

        traineeService.deleteTraineeProfile(username, password, transactionId);

        logger.info("[{}] DELETE /api/trainees/delete-trainee response: success", transactionId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // done
    @GetMapping("/trainee/non-assigned-trainers")
    public ResponseEntity<List<TrainerResponseBasic>> getNonAssignedTrainers(
            @RequestHeader String username,
            @RequestHeader String password
    ) {

        String transactionId = UUID.randomUUID().toString();
        logger.info("[{}] GET /api/trainees/trainee/non-assigned-trainers called: {}", transactionId, username);

        List<TrainerResponseBasic> response = traineeService.findNonAssignedTrainers(username, password, transactionId);

        logger.info("[{}] GET /api/trainees/trainee/non-assigned-trainers response: {}", transactionId, response);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/trainee/update-trainers")
    public ResponseEntity<List<TrainerResponseBasic>> updateTrainers(
            @Valid @RequestBody UpdateTraineeTrainersRequest req,
            @RequestHeader String username,
            @RequestHeader String password
    ) {

        String transactionId = UUID.randomUUID().toString();
        logger.info("[{}] PUT /api/trainees/trainee/delete-trainee called: {}", transactionId, username);

        List<TrainerResponseBasic> response = traineeService.updateTraineeTrainers(req, username, password, transactionId);

        logger.info("[{}] PUT /api/trainees/trainee/delete-trainee response: {}", transactionId, response);

        return ResponseEntity.ok(response);
    }

    // done
    @GetMapping("/trainee/trainings")
    public ResponseEntity<List<TrainingResponseForTrainee>> getTraineeTrainings(
            @RequestHeader String username,
            @RequestHeader String password,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) String trainingTypeName
    ) {

        String transactionId = UUID.randomUUID().toString();
        logger.info("[{}] GET /api/trainees/trainee/trainings: {}", transactionId, username);

        List<TrainingResponseForTrainee> response = traineeService.findTraineeTrainings(username, password, fromDate, toDate, trainerName, trainingTypeName, transactionId);

        logger.info("[{}] GET /api/trainees/trainee/trainings response: {}", transactionId, response);

        return ResponseEntity.ok(response);
    }

    // to test
    @PatchMapping("/trainee/activate-deactivate")
    public ResponseEntity<?> changeActivationStatus(@Valid @RequestBody TraineeActivationRequest req,
                                                    @RequestHeader String username,
                                                    @RequestHeader String password) {

        String transactionId = UUID.randomUUID().toString();
        logger.info("[{}] PATCH /api/trainees/trainee/activate-deactivate called: {}", transactionId, req);

        traineeService.activateDeactivateTrainee(req, username, password, transactionId);

        logger.info("[{}] PATCH /api/trainees/trainee/activate-deactivate response: success", transactionId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
