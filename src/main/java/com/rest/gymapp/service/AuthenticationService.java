package com.rest.gymapp.service;


public interface AuthenticationService {

    boolean authenticateTrainee(String username, String password);

    boolean authenticateTrainer(String username, String password);
}





