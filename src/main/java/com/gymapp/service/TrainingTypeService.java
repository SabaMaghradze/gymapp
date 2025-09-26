package com.gymapp.service;

import com.gymapp.dto.request.trainingType.TrainingTypeRegistrationRequest;
import com.gymapp.dto.response.trainingtype.TrainingTypeResponse;

import java.util.List;

public interface TrainingTypeService {

    List<TrainingTypeResponse> getAllTrainingTypes(String transactionId);

    TrainingTypeResponse addTrainingType(TrainingTypeRegistrationRequest req, String transactionId);
}
