package com.rest.gymapp.controller;

import com.rest.gymapp.dto.request.training.TrainingRegistrationRequest;
import com.rest.gymapp.service.TraineeService;
import com.rest.gymapp.service.TrainingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trainings")
public class TrainingController {

    private final TrainingService trainingService;
    private static final Logger logger = LoggerFactory.getLogger(TrainingController.class);

    // done
    @PostMapping("/add-training")
    public ResponseEntity<?> addTraining(@Valid @RequestBody TrainingRegistrationRequest req,
                                         @RequestHeader String username,
                                         @RequestHeader String password) {

        String transactionId = UUID.randomUUID().toString();
        logger.info("[{}] POST /api/trainings/add-training called by {}", transactionId, username);

        trainingService.addTraining(req, username, password, transactionId);

        logger.info("[{}] Training added successfully", transactionId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
