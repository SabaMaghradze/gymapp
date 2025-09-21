package com.gymapp.service;

import com.gymapp.dto.request.trainingType.TrainingTypeRegistrationRequest;
import com.gymapp.dto.response.trainingtype.TrainingTypeResponse;

import java.util.List;

public interface TrainingTypeService {

    List<TrainingTypeResponse> getAllTrainingTypes(String username, String password, String transactionId);

    TrainingTypeResponse addTrainingType(TrainingTypeRegistrationRequest req, String username, String password, String transactionId);
}
