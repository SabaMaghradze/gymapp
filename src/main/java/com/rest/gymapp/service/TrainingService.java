package com.rest.gymapp.service;

import com.rest.gymapp.dto.request.training.TrainingRegistrationRequest;

public interface TrainingService {
    void addTraining(TrainingRegistrationRequest req, String username, String password, String transactionId);
}
