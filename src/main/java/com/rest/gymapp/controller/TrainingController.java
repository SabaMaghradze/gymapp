package com.rest.gymapp.controller;

import com.rest.gymapp.dto.request.TrainingRequest;
import com.rest.gymapp.service.TrainingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/training")
public class TrainingController {

    private final TrainingService trainingService;

    @PostMapping("/add-training")
    public ResponseEntity<?> addTraining(@Valid @RequestBody TrainingRequest req) {
        trainingService.addTraining(req);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
