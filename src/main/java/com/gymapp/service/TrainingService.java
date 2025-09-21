package com.gymapp.service;

import com.gymapp.dto.request.training.TrainingRegistrationRequest;

public interface TrainingService {
    void addTraining(TrainingRegistrationRequest req, String username, String password, String transactionId);
}
