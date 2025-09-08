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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trainees")
public class TraineeController {

    private final TraineeService traineeService;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerTrainee(
            @Valid @RequestBody TraineeRegistrationRequest req) {

        RegistrationResponse response = traineeService.createTraineeProfile(
                req.firstName(),
                req.lastName(),
                req.dateOfBirth(),
                req.address()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/trainee")
    public ResponseEntity<TraineeProfileResponse> getTrainee(@RequestParam String username,
                                                             @RequestParam String password) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(traineeService.getTraineeProfileByUsername(username, password));
    }

    @PutMapping("/update-trainee")
    public ResponseEntity<TraineeUpdateResponse> updateTrainee(@Valid @RequestBody TraineeUpdateRequest req,
                                                               @RequestParam String password) {
        return ResponseEntity.status(HttpStatus.OK).body(traineeService.updateTraineeProfile(req, password));
    }

    @DeleteMapping("/delete-trainee")
    public ResponseEntity<?> deleteTrainee(@RequestParam String username,
                                           @RequestParam String password) {
        traineeService.deleteTraineeProfile(username, password);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/trainee/non-assigned-trainers")
    public ResponseEntity<List<TrainerResponseBasic>> getNonAssignedTrainers(
            @RequestParam String username,
            @RequestParam String password
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(traineeService.findNonAssignedTrainers(username, password));
    }

    @PutMapping("/trainee/update-trainers")
    public ResponseEntity<List<TrainerResponseBasic>> updateTrainers(
            @Valid @RequestBody UpdateTraineeTrainersRequest req,
            @RequestParam String password
    ) {
        return ResponseEntity.ok(traineeService.updateTraineeTrainers(req, password));
    }

    @GetMapping("/trainee/trainings")
    public ResponseEntity<List<TrainingResponseForTrainee>> getTraineeTrainings(
            @Valid @RequestBody TraineeTrainingsRequest req,
            @RequestParam String password
            ) {
        return ResponseEntity.ok(traineeService.findTraineeTrainings(req, password));
    }

    @PatchMapping("/trainee/activate-deactivate")
    public ResponseEntity<?> changeActivationStatus(@Valid @RequestBody TraineeActivationRequest req,
                                                 @RequestParam String password) {
        traineeService.activateDeactivateTrainee(req, password);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
