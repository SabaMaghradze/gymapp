package com.rest.gymapp.repository;

import com.rest.gymapp.model.Trainee;
import com.rest.gymapp.model.Trainer;
import com.rest.gymapp.model.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TraineeRepository {

    Trainee save(Trainee trainee);

    Optional<Trainee> findById(Long id);

    Optional<Trainee> findByUsername(String username);

    List<Trainee> findAll();

    void delete(Trainee trainee);

    List<Trainer> findTrainersNotAssignedToTrainee(String traineeUsername);

    List<Training> findTrainingsByTraineeUsernameWithCriteria(String traineeUsername,
                                                     LocalDate fromDate,
                                                     LocalDate toDate,
                                                     String trainerName,
                                                     String trainingTypeName);

}
