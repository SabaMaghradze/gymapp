package com.rest.gymapp.repository;

import com.rest.gymapp.model.Trainer;
import com.rest.gymapp.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    Optional<Trainer> findByUsername(String username);

    List<Training> findTrainingsByTrainerUsernameWithCriteria(String trainerUsername,
                                                     LocalDate fromDate,
                                                     LocalDate toDate,
                                                     String traineeName,
                                                     String trainingTypeName);
}
