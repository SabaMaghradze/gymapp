package com.rest.gymapp.utils;

import com.rest.gymapp.dto.response.*;
import com.rest.gymapp.model.*;

import java.util.HashSet;
import java.util.Set;

public class Mappers {

    public static UserResponse getUserResponse(User user) {
        UserResponse userResponse = new UserResponse(user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getIsActive(),
                Mappers.getTraineeResponse(user.getTrainee()),
                Mappers.getTrainerResponse(user.getTrainer()));

        return userResponse;
    }

    public static TrainerResponse getTrainerResponse(Trainer trainer) {

        TrainerResponse trainerResponse = new TrainerResponse(trainer.getId(),
                Mappers.getTrainingTypeResponse(trainer.getSpecialization()),
                Mappers.getUserResponse(trainer.getUser()));

        // set trainee and training responses.

        Set<TraineeResponse> traineeResponses = new HashSet<>();
        Set<Trainee> trainees = trainer.getTrainees();

        for (Trainee trainee : trainees) {
            traineeResponses.add(Mappers.getTraineeResponse(trainee));
        }

        Set<TrainingResponse> trainingResponses = new HashSet<>();
        Set<Training> trainings = trainer.getTrainings();

        for (Training training : trainings) {
            trainingResponses.add(Mappers.getTrainingResponse(training));
        }

        trainerResponse.setTraineeResponses(traineeResponses);
        trainerResponse.setTrainingResponses(trainingResponses);

        return trainerResponse;
    }

    public static TrainingTypeResponse getTrainingTypeResponse(TrainingType trainingType) {
        return new TrainingTypeResponse(trainingType.getId(),
                trainingType.getTrainingTypeName());
    }

    public static TrainingResponse getTrainingResponse(Training training) {
        return new TrainingResponse(training.getId(),
                Mappers.getTraineeResponse(training.getTrainee()),
                Mappers.getTrainerResponse(training.getTrainer()),
                Mappers.getTrainingTypeResponse(training.getTrainingType()),
                training.getTrainingName(),
                training.getTrainingDate(),
                training.getTrainingDuration());
    }

    private static TraineeResponse getTraineeResponse(Trainee trainee) {

        TraineeResponse traineeResponse = new TraineeResponse(trainee.getId(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                Mappers.getUserResponse(trainee.getUser()));

        // set training and trainer responses.

        Set<TrainerResponse> trainerResponses = new HashSet<>();
        Set<Trainer> trainers = trainee.getTrainers();

        for (Trainer trainer : trainers) {
            trainerResponses.add(Mappers.getTrainerResponse(trainer));
        }

        Set<TrainingResponse> trainingResponses = new HashSet<>();
        Set<Training> trainings = trainee.getTrainings();

        for (Training training : trainings) {
            trainingResponses.add(Mappers.getTrainingResponse(training));
        }

        traineeResponse.setTrainerResponses(trainerResponses);
        traineeResponse.setTrainingResponses(trainingResponses);

        return traineeResponse;
    }
}
