package com.rest.gymapp.repository;

import com.rest.gymapp.model.Trainee;
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
public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    Optional<Trainee> findByUserUsername(String username);

    @Query("SELECT tr FROM Trainer tr WHERE tr NOT IN (" +
            "  SELECT t FROM Trainee ta JOIN ta.trainers t WHERE ta.user.username = :username" +
            ")")
    List<Trainer> findTrainersNotAssignedToTrainee(String traineeUsername);

    @Query("""
            SELECT tr FROM Training tr
            JOIN tr.trainee t JOIN t.user tu
            JOIN tr.trainer trn JOIN trn.user trun
            JOIN tr.trainingType tt
            WHERE tu.username = :traineeUsername
            AND (:fromDate IS NULL OR tr.trainingDate >= :fromDate)
            AND (:toDate IS NULL OR tr.trainingDate <= :toDate)
            AND (
                :trainerName IS NULL OR
                LOWER(trun.firstName) LIKE LOWER(CONCAT('%', :trainerName, '%')) OR
                LOWER(trun.lastName) LIKE LOWER(CONCAT('%', :trainerName, '%')) OR
                LOWER(CONCAT(trun.firstName, ' ', trun.lastName)) LIKE LOWER(CONCAT('%', :trainerName, '%'))
            )
            AND (:trainingTypeName IS NULL OR LOWER(tt.trainingTypeName) = LOWER(:trainingTypeName))
            """)
    List<Training> findTrainingsByTraineeUsernameWithCriteria(
            @Param("traineeUsername") String traineeUsername,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("trainerName") String trainerName,
            @Param("trainingTypeName") String trainingTypeName);
}
