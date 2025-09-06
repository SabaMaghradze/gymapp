package com.rest.gymapp.utils;

import com.rest.gymapp.dto.response.*;
import com.rest.gymapp.model.*;

import java.util.HashSet;
import java.util.Set;

public class Mappers {

    public UserResponse getUserResponse(User user) {
        UserResponse userResponse = new UserResponse(user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getIsActive(),
                getTraineeResponse(user.getTrainee()),
                getTrainerResponse(user.getTrainer()));

        return userResponse;
    }

    public TrainerResponse getTrainerResponse(Trainer trainer) {

        TrainerResponse trainerResponse = new TrainerResponse(trainer.getId(),
                getTrainingTypeResponse(trainer.getSpecialization()),
                getUserResponse(trainer.getUser()));

        // set trainee and training responses.

        Set<TraineeResponse> traineeResponses = new HashSet<>();
        Set<Trainee> trainees = trainer.getTrainees();

        for (Trainee trainee : trainees) {
            traineeResponses.add(getTraineeResponse(trainee));
        }

        Set<TrainingResponse> trainingResponses = new HashSet<>();
        Set<Training> trainings = trainer.getTrainings();

        for (Training training : trainings) {
            trainingResponses.add(getTrainingResponse(training));
        }

        trainerResponse.setTraineeResponses(traineeResponses);
        trainerResponse.setTrainingResponses(trainingResponses);

        return trainerResponse;
    }

    public TrainingTypeResponse getTrainingTypeResponse(TrainingType trainingType) {
        return new TrainingTypeResponse(trainingType.getId(),
                trainingType.getTrainingTypeName());
    }

    public TrainingResponse getTrainingResponse(Training training) {
        return new TrainingResponse(training.getId(),
                getTraineeResponse(training.getTrainee()),
                getTrainerResponse(training.getTrainer()),
                getTrainingTypeResponse(training.getTrainingType()),
                training.getTrainingName(),
                training.getTrainingDate(),
                training.getTrainingDuration());
    }

    public TraineeResponse getTraineeResponse(Trainee trainee) {

        TraineeResponse traineeResponse = new TraineeResponse(trainee.getId(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                getUserResponse(trainee.getUser()));

        // set training and trainer responses.

        Set<TrainerResponse> trainerResponses = new HashSet<>();
        Set<Trainer> trainers = trainee.getTrainers();

        for (Trainer trainer : trainers) {
            trainerResponses.add(getTrainerResponse(trainer));
        }

        Set<TrainingResponse> trainingResponses = new HashSet<>();
        Set<Training> trainings = trainee.getTrainings();

        for (Training training : trainings) {
            trainingResponses.add(getTrainingResponse(training));
        }

        traineeResponse.setTrainerResponses(trainerResponses);
        traineeResponse.setTrainingResponses(trainingResponses);

        return traineeResponse;
    }
}
