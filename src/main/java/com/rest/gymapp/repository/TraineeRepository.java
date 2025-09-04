package com.rest.gymapp.repository;

import com.rest.gymapp.model.Trainee;
import com.rest.gymapp.model.Trainer;
import com.rest.gymapp.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    Optional<Trainee> findByUsername(String username);

    List<Trainer> findTrainersNotAssignedToTrainee(String traineeUsername);

    List<Training> findTrainingsByTraineeUsernameWithCriteria(String traineeUsername,
                                                     LocalDate fromDate,
                                                     LocalDate toDate,
                                                     String trainerName,
                                                     String trainingTypeName);
}
