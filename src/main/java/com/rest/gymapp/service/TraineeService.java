package com.rest.gymapp.service;

import com.rest.gymapp.dto.request.trainee.TraineeUpdateRequest;
import com.rest.gymapp.dto.request.trainee.UpdateTraineeTrainersRequest;
import com.rest.gymapp.dto.request.training.TraineeTrainingsRequest;
import com.rest.gymapp.dto.response.RegistrationResponse;
import com.rest.gymapp.dto.response.trainee.TraineeProfileResponse;
import com.rest.gymapp.dto.response.trainee.TraineeUpdateResponse;
import com.rest.gymapp.dto.response.trainer.TrainerResponseBasic;
import com.rest.gymapp.dto.response.training.TrainingResponse;
import com.rest.gymapp.model.Trainer;
import com.rest.gymapp.model.Training;

import java.time.LocalDate;
import java.util.List;


public interface TraineeService {

    RegistrationResponse createTraineeProfile(String firstName, String lastName,
                                              LocalDate dateOfBirth, String address);

    TraineeProfileResponse getTraineeProfileByUsername(String username, String password);

//    boolean changeTraineePassword(String username, String oldPassword, String newPassword);

    TraineeUpdateResponse updateTraineeProfile(TraineeUpdateRequest request, String password);

    boolean activateDeactivateTrainee(String username, String password, boolean active);

    void deleteTraineeProfile(String username, String password);

    List<TrainerResponseBasic> findNonAssignedTrainers(String traineeUsername, String password);

    List<TrainingResponse> findTraineeTrainings(TraineeTrainingsRequest req, String password);

    List<TrainerResponseBasic> updateTraineeTrainers(UpdateTraineeTrainersRequest req, String password);
}
