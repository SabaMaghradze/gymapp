package com.gymapp.service;

import com.gymapp.dto.request.auth.LoginRequest;
import com.gymapp.dto.request.auth.UserRegistrationRequest;
import com.gymapp.dto.response.auth.JwtResponse;
import com.gymapp.dto.response.auth.UserRegistrationResponse;

public interface AuthService {

    JwtResponse authenticateUser(LoginRequest loginRequest);

    UserRegistrationResponse registerUser(UserRegistrationRequest req);
}
