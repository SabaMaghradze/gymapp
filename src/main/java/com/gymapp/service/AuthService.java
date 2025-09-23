package com.gymapp.service;

import com.gymapp.dto.request.auth.LoginRequest;

public interface AuthService {

    void authenticateUser(LoginRequest loginRequest);
}
