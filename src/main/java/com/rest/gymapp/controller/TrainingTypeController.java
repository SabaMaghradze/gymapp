package com.rest.gymapp.controller;

import com.rest.gymapp.dto.request.trainingType.TrainingTypeRegistrationRequest;
import com.rest.gymapp.dto.response.trainingtype.TrainingTypeResponse;
import com.rest.gymapp.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/training-types")
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;

    @GetMapping("/all-training-types")
    public ResponseEntity<List<TrainingTypeResponse>> getAllTrainingTypes(
            @RequestParam String username,
            @RequestParam String password
    ) {
        return ResponseEntity.ok(trainingTypeService.getAllTrainingTypes(username, password));
    }

    @PostMapping("/add-type")
    public ResponseEntity<TrainingTypeResponse> addTrainingType(@RequestBody TrainingTypeRegistrationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trainingTypeService.addTrainingType(req));
    }
}
