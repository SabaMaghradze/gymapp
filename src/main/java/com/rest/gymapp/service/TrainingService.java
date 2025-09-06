package com.rest.gymapp.service;

import com.rest.gymapp.dto.request.TrainingRequest;
import com.rest.gymapp.model.Training;


public interface TrainingService {

    void addTraining(TrainingRequest trainingRequest);
}
