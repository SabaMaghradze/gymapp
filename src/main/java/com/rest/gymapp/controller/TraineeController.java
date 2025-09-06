package com.rest.gymapp.controller;


import com.rest.gymapp.dto.request.TraineeRegistrationRequest;
import com.rest.gymapp.dto.response.RegistrationResponse;
import com.rest.gymapp.dto.response.TraineeResponse;
import com.rest.gymapp.service.TraineeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trainees")
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

    @GetMapping("/trainee")
    public ResponseEntity<TraineeResponse> getTrainee(@RequestParam String username,
                                                      @RequestParam String password) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(traineeService.getTraineeProfileByUsername(username, password));
    }
}
