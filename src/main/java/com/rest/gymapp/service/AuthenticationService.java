package com.rest.gymapp.service;


public interface AuthenticationService {

    void authenticateTrainee(String username, String password);

    void authenticateTrainer(String username, String password);

    void authenticateUser(String username, String password);

    void changePassword(String username, String oldPassword, String newPassword);
}





