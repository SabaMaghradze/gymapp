package com.gymapp.service;

import com.gymapp.dto.request.training.TrainingRegistrationRequest;
import com.gymapp.dto.response.training.TrainingResponse;

import java.util.List;

public interface TrainingService {

    void addTraining(TrainingRegistrationRequest req, String transactionId);

    void cancelTraining(Long trainingId, String transactionId);

    List<TrainingResponse> getAllTrainings(String transactionId);
}
