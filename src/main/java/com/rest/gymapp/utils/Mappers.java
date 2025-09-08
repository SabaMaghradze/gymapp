package com.rest.gymapp.utils;

import com.rest.gymapp.dto.response.*;
import com.rest.gymapp.dto.response.trainee.TraineeProfileResponse;
import com.rest.gymapp.dto.response.trainee.TraineeUpdateResponse;
import com.rest.gymapp.dto.response.trainee.TraineeResponseForGetTrainer;
import com.rest.gymapp.dto.response.trainer.TrainerProfileResponse;
import com.rest.gymapp.dto.response.trainer.TrainerResponseBasic;
import com.rest.gymapp.dto.response.trainer.TrainerUpdateResponse;
import com.rest.gymapp.dto.response.training.TrainingResponseForTrainee;
import com.rest.gymapp.dto.response.training.TrainingResponseForTrainer;
import com.rest.gymapp.dto.response.trainingtype.TrainingTypeResponse;
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

    public TrainerProfileResponse getTrainerProfileResponse(Trainer trainer) {

        TrainerProfileResponse trainerProfileResponse = new TrainerProfileResponse(
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                getTrainingTypeResponse(trainer.getSpecialization()),
                trainer.getUser().getIsActive()
        );

        // set trainee and training responses.

        Set<TraineeResponseForGetTrainer> traineeResponses = new HashSet<>();
        Set<Trainee> trainees = trainer.getTrainees();

        for (Trainee trainee : trainees) {
            traineeResponses.add(new TraineeResponseForGetTrainer(
                    trainee.getUser().getUsername(),
                    trainee.getUser().getFirstName(),
                    trainee.getUser().getLastName()
            ));
        }
//
//        Set<TrainingResponse> trainingResponses = new HashSet<>();
//        Set<Training> trainings = trainer.getTrainings();
//
//        for (Training training : trainings) {
//            trainingResponses.add(getTrainingResponse(training));
//        }
//
        trainerProfileResponse.setTraineeResponses(traineeResponses);
//        trainerResponse.setTrainingResponses(trainingResponses);

        return trainerProfileResponse;
    }

    public TrainerUpdateResponse getTrainerUpdateResponse(Trainer trainer) {

        TrainerUpdateResponse trainerUpdateResponse = new TrainerUpdateResponse(
                trainer.getUser().getUsername(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                getTrainingTypeResponse(trainer.getSpecialization()),
                trainer.getUser().getIsActive()
        );

        Set<TraineeResponseForGetTrainer> traineeResponses = new HashSet<>();
        Set<Trainee> trainees = trainer.getTrainees();

        for (Trainee trainee : trainees) {
            traineeResponses.add(new TraineeResponseForGetTrainer(
                    trainee.getUser().getUsername(),
                    trainee.getUser().getFirstName(),
                    trainee.getUser().getLastName()
            ));
        }

        trainerUpdateResponse.setTraineeResponses(traineeResponses);

        return trainerUpdateResponse;
    }

    public TrainingTypeResponse getTrainingTypeResponse(TrainingType trainingType) {
        return new TrainingTypeResponse(trainingType.getId(),
                trainingType.getTrainingTypeName());
    }

    public TrainingResponseForTrainee getTrainingResponseForTrainee(Training training) {
        return new TrainingResponseForTrainee(
                training.getTrainingName(),
                training.getTrainingDate(),
                getTrainingTypeResponse(training.getTrainingType()),
                training.getTrainingDuration(),
                training.getTrainer().getUser().getUsername()
        );
    }

    public TraineeUpdateResponse getTraineeUpdateResponse(Trainee trainee) {

        TraineeUpdateResponse traineeUpdateResponse = new TraineeUpdateResponse(
                trainee.getUser().getUsername(),
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                trainee.getUser().getIsActive()
        );

        // set training and trainer responses.

        Set<TrainerResponseBasic> trainerProfileRespons = new HashSet<>();
        Set<Trainer> trainers = trainee.getTrainers();

        for (Trainer trainer : trainers) {
            trainerProfileRespons.add(new TrainerResponseBasic(
                    trainer.getUser().getFirstName(),
                    trainer.getUser().getLastName(),
                    trainer.getUser().getUsername(),
                    getTrainingTypeResponse(trainer.getSpecialization())
            ));
        }

//        traineeResponse.setTrainingResponses(trainingResponses);

//        Set<TrainingResponse> trainingResponses = new HashSet<>();
//        Set<Training> trainings = trainee.getTrainings();
//
//        for (Training training : trainings) {
//            trainingResponses.add(getTrainingResponse(training));
//        }

        traineeUpdateResponse.setTrainerProfileRespons(trainerProfileRespons);

        return traineeUpdateResponse;
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
        Set<TrainerResponseBasic> trainerResponses = new HashSet<>();

        for (Trainer trainer : trainers) {
            trainerResponses.add(getTrainerResponseBasic(trainer));
        }

        response.setTrainerResponses(trainerResponses);

        return response;
    }

    public TrainerResponseBasic getTrainerResponseBasic(Trainer trainer) {
        return new TrainerResponseBasic(
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getUser().getUsername(),
                getTrainingTypeResponse(trainer.getSpecialization())
        );
    }

    // for findTrainerTrainingsByCriteria
    public TrainingResponseForTrainer getTrainingResponseForTrainer(Training training) {
        return new TrainingResponseForTrainer(
                training.getTrainingName(),
                training.getTrainingDate(),
                getTrainingTypeResponse(training.getTrainingType()),
                training.getTrainingDuration(),
                training.getTrainee().getUser().getUsername()
        );
    }
}
