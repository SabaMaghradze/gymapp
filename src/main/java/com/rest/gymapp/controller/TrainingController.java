package com.rest.gymapp.controller;

import com.rest.gymapp.dto.request.training.TrainingRegistrationRequest;
import com.rest.gymapp.service.TrainingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trainings")
public class TrainingController {

    private final TrainingService trainingService;

    // done
    @PostMapping("/add-training")
    public ResponseEntity<?> addTraining(@Valid @RequestBody TrainingRegistrationRequest req,
                                         @RequestHeader String username,
                                         @RequestHeader String password) {
        trainingService.addTraining(req, username, password);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
