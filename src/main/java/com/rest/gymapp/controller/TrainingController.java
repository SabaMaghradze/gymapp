package com.rest.gymapp.controller;

import com.rest.gymapp.dto.request.training.TrainingRegistrationRequest;
import com.rest.gymapp.service.TrainingService;
import io.swagger.annotations.*;
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
@Api(tags = "Training Management")
public class TrainingController {

    private final TrainingService trainingService;
    private static final Logger logger = LoggerFactory.getLogger(TrainingController.class);

    @ApiOperation(
            value = "Register a new training",
            notes = "Allows a trainee to register a training with a specific trainer and type."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Training successfully registered"),
            @ApiResponse(code = 401, message = "Unauthorized – invalid credentials"),
            @ApiResponse(code = 404, message = "Trainer, trainee, or training type not found"),
            @ApiResponse(code = 400, message = "Bad request – invalid input")
    })
    @PostMapping
    public ResponseEntity<?> addTraining(
            @Valid @RequestBody TrainingRegistrationRequest req,
            @ApiParam(value = "Trainee's username", required = true) @RequestHeader String username,
            @ApiParam(value = "Trainee's password", required = true) @RequestHeader String password) {

        String transactionId = UUID.randomUUID().toString();
        logger.info("[{}] POST /api/trainings called by {}", transactionId, username);

        trainingService.addTraining(req, username, password, transactionId);

        logger.info("[{}] Training added successfully", transactionId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
