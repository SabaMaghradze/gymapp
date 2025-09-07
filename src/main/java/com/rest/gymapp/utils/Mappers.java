package com.rest.gymapp.utils;

import com.rest.gymapp.dto.response.*;
import com.rest.gymapp.dto.response.trainee.TraineeProfileResponse;
import com.rest.gymapp.dto.response.trainee.TraineeResponse;
import com.rest.gymapp.dto.response.trainer.TrainerResponse;
import com.rest.gymapp.dto.response.trainer.TrainerResponseForTraineeProfile;
import com.rest.gymapp.model.*;

import java.util.HashSet;
import java.util.Set;

public class Mappers {

    public UserResponse getUserResponse(User user) {
        UserResponse userResponse = new UserResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getIsActive());

        return userResponse;
    }

    public TrainerResponse getTrainerResponse(Trainer trainer) {

        TrainerResponse trainerResponse = new TrainerResponse(
                trainer.getUser().getUsername(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                getTrainingTypeResponse(trainer.getSpecialization())
        );

        // set trainee and training responses.

        Set<TraineeResponse> traineeResponses = new HashSet<>();
        Set<Trainee> trainees = trainer.getTrainees();

        for (Trainee trainee : trainees) {
            traineeResponses.add(getTraineeResponse(trainee));
        }
//
//        Set<TrainingResponse> trainingResponses = new HashSet<>();
//        Set<Training> trainings = trainer.getTrainings();
//
//        for (Training training : trainings) {
//            trainingResponses.add(getTrainingResponse(training));
//        }
//
        trainerResponse.setTraineeResponses(traineeResponses);
//        trainerResponse.setTrainingResponses(trainingResponses);

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

        TraineeResponse traineeResponse = new TraineeResponse(
                trainee.getUser().getUsername(),
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                trainee.getUser().getIsActive()
        );

        // set training and trainer responses.

        Set<TrainerResponse> trainerResponses = new HashSet<>();
        Set<Trainer> trainers = trainee.getTrainers();

        for (Trainer trainer : trainers) {
            trainerResponses.add(getTrainerResponse(trainer));
        }

//        traineeResponse.setTrainingResponses(trainingResponses);

//        Set<TrainingResponse> trainingResponses = new HashSet<>();
//        Set<Training> trainings = trainee.getTrainings();
//
//        for (Training training : trainings) {
//            trainingResponses.add(getTrainingResponse(training));
//        }

        traineeResponse.setTrainerResponses(trainerResponses);

        return traineeResponse;
    }

    public TraineeProfileResponse getTraineeProfileResponse(Trainee trainee) {

        TraineeProfileResponse response = new TraineeProfileResponse(
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                trainee.getUser().getIsActive()
        );

        Set<Trainer> trainers = trainee.getTrainers();
        Set<TrainerResponseForTraineeProfile> trainerResponses = new HashSet<>();

        for (Trainer trainer : trainers) {
            trainerResponses.add(getTrainerResponseTwo(trainer));
        }

        response.setTrainerResponses(trainerResponses);

        return response;
    }

    private TrainerResponseForTraineeProfile getTrainerResponseTwo(Trainer trainer) {
        return new TrainerResponseForTraineeProfile(
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getUser().getUsername(),
                getTrainingTypeResponse(trainer.getSpecialization())
        );
    }
}
