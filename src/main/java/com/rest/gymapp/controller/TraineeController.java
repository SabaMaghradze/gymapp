package com.rest.gymapp.controller;


import com.rest.gymapp.dto.request.TraineeRegistrationRequest;
import com.rest.gymapp.dto.response.RegistrationResponse;
import com.rest.gymapp.service.TraineeService;
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
@RequestMapping("/api/trainee")
public class TraineeController {

    private final TraineeService traineeService;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerTrainee(
            @Valid @RequestBody TraineeRegistrationRequest req) {

        RegistrationResponse response = traineeService.createTraineeProfile(
                req.firstName(),
                req.lastName(),
                req.dateOfBirth(),
                req.address()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
