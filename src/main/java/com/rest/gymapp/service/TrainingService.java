package com.rest.gymapp.service;

import com.rest.gymapp.dto.request.training.TrainingRequest;


public interface TrainingService {

    void addTraining(TrainingRequest trainingRequest);
}
