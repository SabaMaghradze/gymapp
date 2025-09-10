package com.rest.gymapp.repository;

import com.rest.gymapp.model.Trainer;
import com.rest.gymapp.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    Optional<Trainer> findByUserUsername(String username);

    @Query("""
    SELECT tr FROM Training tr
    JOIN tr.trainer trn
    JOIN trn.user trun
    JOIN tr.trainee t
    JOIN t.user tu
    WHERE trun.username = :trainerUsername
      AND (:fromDate IS NULL OR tr.trainingDate >= :fromDate)
      AND (:toDate IS NULL OR tr.trainingDate <= :toDate)
      AND (
          :traineeName IS NULL
          OR LOWER(tu.firstName) LIKE LOWER(CONCAT('%', :traineeName, '%'))
          OR LOWER(tu.lastName) LIKE LOWER(CONCAT('%', :traineeName, '%'))
          OR LOWER(CONCAT(tu.firstName, ' ', tu.lastName)) LIKE LOWER(CONCAT('%', :traineeName, '%'))
      )
""")
    List<Training> findTrainingsByTrainerUsernameWithCriteria(
            @Param("trainerUsername") String trainerUsername,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("traineeName") String traineeName
    );
}
