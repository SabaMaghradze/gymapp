package com.rest.gymapp.controller;

import com.rest.gymapp.dto.request.trainer.TrainerActivationRequest;
import com.rest.gymapp.dto.request.trainer.TrainerRegistrationRequest;
import com.rest.gymapp.dto.request.trainer.TrainerUpdateRequest;
import com.rest.gymapp.dto.request.training.TrainerTrainingsRequest;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trainers")
public class TrainerController {

    private final TrainerService trainerService;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerTrainer(
            @Valid @RequestBody TrainerRegistrationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trainerService.createTrainerProfile(req));
    }

    // test this again after adding trainees to make sure there is trainee responses in the dto.
    @GetMapping("/trainer")
    public ResponseEntity<TrainerProfileResponse> getTrainer(@RequestHeader String username,
                                                             @RequestHeader String password) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(trainerService.getTrainerByUsername(username, password));
    }

    @PutMapping("/update-trainer")
    public ResponseEntity<TrainerUpdateResponse> updateTrainer(@Valid @RequestBody TrainerUpdateRequest req,
                                                               @RequestHeader String username,
                                                               @RequestHeader String password) {
        return ResponseEntity.status(HttpStatus.OK).body(trainerService.updateTrainerProfile(req, username, password));
    }

    @GetMapping("/trainer/trainings")
    public ResponseEntity<List<TrainingResponseForTrainer>> findTrainerTrainings(
            @Valid @RequestBody TrainerTrainingsRequest req,
            @RequestParam String password
    ) {
        return ResponseEntity.ok(trainerService.findTrainerTrainingsByCriteria(req, password));
    }

    @PatchMapping("/trainer/activate-deactivate")
    public ResponseEntity<?> changeActivationStatus(@Valid @RequestBody TrainerActivationRequest req,
                                                    @RequestParam String password) {
        trainerService.activateDeactivateTrainer(req, password);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
