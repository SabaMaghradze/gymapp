package com.gymapp.controller;

import com.gymapp.dto.request.auth.LoginRequest;
import com.gymapp.dto.request.auth.UserRegistrationRequest;
import com.gymapp.dto.response.auth.JwtResponse;
import com.gymapp.dto.response.auth.UserRegistrationResponse;
import com.gymapp.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Api(tags = "User Authentication")
public class AuthController {

    private final AuthService authService;

    @ApiOperation(
            value = "User Registration",
            notes = "Registers a user with username and password."
    )
    @ApiResponses({
            @ApiResponse(code = 201, message = "User successfully registered"),
            @ApiResponse(code = 400, message = "Invalid credentials or bad request")
    })
    @PostMapping
    public ResponseEntity<UserRegistrationResponse> registerUser(@Valid @RequestBody UserRegistrationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(req));
    }

    @ApiOperation(
            value = "User login",
            notes = "Authenticates a user with their username and password."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully authenticated"),
            @ApiResponse(code = 400, message = "Invalid credentials or bad request")
    })
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.authenticateUser(loginRequest));
    }
}
