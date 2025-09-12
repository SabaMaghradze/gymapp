package com.rest.gymapp.service;

import com.rest.gymapp.dto.request.trainingType.TrainingTypeRegistrationRequest;
import com.rest.gymapp.dto.response.trainingtype.TrainingTypeResponse;

import java.util.List;

public interface TrainingTypeService {

    List<TrainingTypeResponse> getAllTrainingTypes(String username, String password);

    TrainingTypeResponse addTrainingType(TrainingTypeRegistrationRequest req, String username, String password);
}
