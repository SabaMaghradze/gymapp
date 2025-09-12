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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trainees")
public class TraineeController {

    private final TraineeService traineeService;

    // done
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerTrainee(
            @Valid @RequestBody TraineeRegistrationRequest req) {

        RegistrationResponse response = traineeService.createTraineeProfile(req);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // done
    @GetMapping("/trainee")
    public ResponseEntity<TraineeProfileResponse> getTrainee(@RequestHeader String username,
                                                             @RequestHeader String password) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(traineeService.getTraineeProfileByUsername(username, password));
    }

    // done
    // may change dto, since for now username is not required in the request,
    // trainee cannot change it themselves
    @PutMapping("/update-trainee")
    public ResponseEntity<TraineeUpdateResponse> updateTrainee(@Valid @RequestBody TraineeUpdateRequest req,
                                                               @RequestHeader String username,
                                                               @RequestHeader String password) {
        return ResponseEntity.status(HttpStatus.OK).body(traineeService.updateTraineeProfile(req, username, password));
    }

    // done
    @DeleteMapping("/delete-trainee")
    public ResponseEntity<?> deleteTrainee(@RequestHeader String username,
                                           @RequestHeader String password) {
        traineeService.deleteTraineeProfile(username, password);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // to fix (most-likely issue - lazy loading)
    @GetMapping("/trainee/non-assigned-trainers")
    public ResponseEntity<List<TrainerResponseBasic>> getNonAssignedTrainers(
            @RequestHeader String username,
            @RequestHeader String password
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(traineeService.findNonAssignedTrainers(username, password));
    }

    // to test
    @PutMapping("/trainee/update-trainers")
    public ResponseEntity<List<TrainerResponseBasic>> updateTrainers(
            @Valid @RequestBody UpdateTraineeTrainersRequest req,
            @RequestHeader String username,
            @RequestHeader String password
    ) {
        return ResponseEntity.ok(traineeService.updateTraineeTrainers(req, username, password));
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
        return ResponseEntity.ok(traineeService.findTraineeTrainings(username, password, fromDate, toDate, trainerName, trainingTypeName));
    }

    // to test
    @PatchMapping("/trainee/activate-deactivate")
    public ResponseEntity<?> changeActivationStatus(@Valid @RequestBody TraineeActivationRequest req,
                                                    @RequestParam String password) {
        traineeService.activateDeactivateTrainee(req, password);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
