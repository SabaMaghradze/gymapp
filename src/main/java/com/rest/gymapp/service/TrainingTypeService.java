package com.rest.gymapp.service;

import com.rest.gymapp.dto.response.trainingtype.TrainingTypeResponse;

import java.util.List;

public interface TrainingTypeService {

    List<TrainingTypeResponse> getAllTrainingTypes(String username, String password);
}
