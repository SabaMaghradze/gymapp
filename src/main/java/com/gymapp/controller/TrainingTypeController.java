package com.gymapp.controller;

import com.gymapp.dto.request.trainingType.TrainingTypeRegistrationRequest;
import com.gymapp.dto.response.trainingtype.TrainingTypeResponse;
import com.gymapp.service.TrainingTypeService;
import io.swagger.annotations.*;
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
@Api(tags = "Training Type Management")
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;
    private static final Logger logger = LoggerFactory.getLogger(TrainingTypeController.class);

    @ApiOperation(
            value = "Get all training types",
            notes = "Retrieves a list of all training types available in the system."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully retrieved training types"),
            @ApiResponse(code = 401, message = "Unauthorized – invalid credentials")
    })
    @GetMapping("/all")
    public ResponseEntity<List<TrainingTypeResponse>> getAllTrainingTypes(
            @ApiParam(value = "Username of the requester", required = true) @RequestHeader String username,
            @ApiParam(value = "Password of the requester", required = true) @RequestHeader String password
    ) {

        String transactionId = UUID.randomUUID().toString();
        logger.info("[{}] GET /api/training-types/all called by: {}", transactionId, username);

        List<TrainingTypeResponse> result = trainingTypeService.getAllTrainingTypes(username, password, transactionId);

        logger.info("[{}] Training types retrieved successfully: {}", transactionId, result);

        return ResponseEntity.ok().body(result);
    }

    @ApiOperation(
            value = "Add a new training type",
            notes = "Registers a new training type in the system."
    )
    @ApiResponses({
            @ApiResponse(code = 201, message = "Training type successfully added"),
            @ApiResponse(code = 400, message = "Bad request – invalid input"),
            @ApiResponse(code = 401, message = "Unauthorized – invalid credentials")
    })
    @PostMapping
    public ResponseEntity<TrainingTypeResponse> addTrainingType(
            @RequestBody TrainingTypeRegistrationRequest req,
            @ApiParam(value = "Username of the requester", required = true) @RequestHeader String username,
            @ApiParam(value = "Password of the requester", required = true) @RequestHeader String password) {

        String transactionId = UUID.randomUUID().toString();
        logger.info("[{}] POST /api/training-types called with payload: {}", transactionId, req);

        TrainingTypeResponse result = trainingTypeService.addTrainingType(req, username, password, transactionId);

        logger.info("[{}] Training type registered successfully: {}", transactionId, result);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
