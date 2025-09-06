package com.rest.gymapp.controller;

import com.rest.gymapp.dto.request.LoginRequest;
import com.rest.gymapp.dto.request.PasswordChangeRequest;
import com.rest.gymapp.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {

    private final AuthenticationService authenticationService;

    @GetMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest req) {
        authenticationService.authenticateUser(req.username(), req.password());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest req) {
        authenticationService.changePassword(req.username(), req.oldPassword(), req.newPassword());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}












