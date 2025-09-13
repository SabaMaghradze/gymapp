package com.rest.gymapp.controller;

import com.rest.gymapp.dto.request.trainingType.TrainingTypeRegistrationRequest;
import com.rest.gymapp.dto.response.trainingtype.TrainingTypeResponse;
import com.rest.gymapp.service.TrainerService;
import com.rest.gymapp.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/training-types")
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;
    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);
    // done
    @GetMapping("/all-training-types")
    public ResponseEntity<List<TrainingTypeResponse>> getAllTrainingTypes(
            @RequestHeader String username,
            @RequestHeader String password
    ) {

        String transactionId = UUID.randomUUID().toString();
        logger.info("[{}] GET /api/training-types/all-training-types called by: {}", transactionId, username);

        List<TrainingTypeResponse> result = trainingTypeService.getAllTrainingTypes(username, password, transactionId);

        logger.info("[{}] Training types retrieved successfully: {}", transactionId, result);

        return ResponseEntity.ok().body(result);
    }

    // done
    @PostMapping("/add-type")
    public ResponseEntity<TrainingTypeResponse> addTrainingType(@RequestBody TrainingTypeRegistrationRequest req,
                                                                @RequestHeader String username,
                                                                @RequestHeader String password) {

        String transactionId = UUID.randomUUID().toString();
        logger.info("[{}] POST /api/training-types/all-training-types called with payload: {}", transactionId, req);

        TrainingTypeResponse result = trainingTypeService.addTrainingType(req, username, password, transactionId);

        logger.info("[{}] Training type registered successfully: {}", transactionId, result);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
