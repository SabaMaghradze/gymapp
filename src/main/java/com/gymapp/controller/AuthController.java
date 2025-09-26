package com.gymapp.controller;

import com.gymapp.dto.request.auth.LoginRequest;
import com.gymapp.dto.request.auth.UserRegistrationRequest;
import com.gymapp.dto.response.auth.JwtResponse;
import com.gymapp.dto.response.auth.UserRegistrationResponse;
import com.gymapp.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<UserRegistrationResponse> registerUser(@Valid @RequestBody UserRegistrationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(req));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.authenticateUser(loginRequest));
    }
}
