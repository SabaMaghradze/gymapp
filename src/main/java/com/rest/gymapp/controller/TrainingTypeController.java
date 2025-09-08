package com.rest.gymapp.controller;

import com.rest.gymapp.dto.response.trainingtype.TrainingTypeResponse;
import com.rest.gymapp.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
