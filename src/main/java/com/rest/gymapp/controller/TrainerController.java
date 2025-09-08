package com.rest.gymapp.controller;

import com.rest.gymapp.dto.request.trainer.TrainerRegistrationRequest;
import com.rest.gymapp.dto.request.trainer.TrainerUpdateRequest;
import com.rest.gymapp.dto.response.RegistrationResponse;
import com.rest.gymapp.dto.response.trainer.TrainerProfileResponse;
import com.rest.gymapp.dto.response.trainer.TrainerUpdateResponse;
import com.rest.gymapp.service.TrainerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trainers")
public class TrainerController {

    private final TrainerService trainerService;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerTrainer(
            @Valid @RequestBody TrainerRegistrationRequest req) {

        RegistrationResponse response = trainerService.createTrainerProfile(
                req.firstName(),
                req.lastName(),
                req.specialization()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/trainer")
    public ResponseEntity<TrainerProfileResponse> getTrainer(@RequestParam String username,
                                                             @RequestParam String password) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(trainerService.getTrainerByUsername(username, password));
    }

    @PutMapping("/update-trainer")
    public ResponseEntity<TrainerUpdateResponse> updateTrainer(@Valid @RequestBody TrainerUpdateRequest req,
                                                               @RequestParam String password) {

        return ResponseEntity.status(HttpStatus.OK).body(trainerService.updateTrainerProfile(req, password));
    }
}
