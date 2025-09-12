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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trainers")
public class TrainerController {

    private final TrainerService trainerService;

    // done
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerTrainer(
            @Valid @RequestBody TrainerRegistrationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trainerService.createTrainerProfile(req));
    }

    // done
    // test this again after adding trainees to make sure there is trainee responses in the dto.
    @GetMapping("/trainer")
    public ResponseEntity<TrainerProfileResponse> getTrainer(@RequestHeader String username,
                                                             @RequestHeader String password) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(trainerService.getTrainerByUsername(username, password));
    }

    // may change dto, since for now username is not required in the request,
    // trainer cannot change it themselves
    @PutMapping("/update-trainer")
    public ResponseEntity<TrainerUpdateResponse> updateTrainer(@Valid @RequestBody TrainerUpdateRequest req,
                                                               @RequestHeader String username,
                                                               @RequestHeader String password) {
        return ResponseEntity.status(HttpStatus.OK).body(trainerService.updateTrainerProfile(req, username, password));
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
        return ResponseEntity.ok(trainerService.findTrainerTrainingsByCriteria(username,
                password, fromDate, toDate, traineeName));
    }

    // to test
    @PatchMapping("/trainer/activate-deactivate")
    public ResponseEntity<?> changeActivationStatus(@Valid @RequestBody TrainerActivationRequest req,
                                                    @RequestParam String password) {
        trainerService.activateDeactivateTrainer(req, password);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
