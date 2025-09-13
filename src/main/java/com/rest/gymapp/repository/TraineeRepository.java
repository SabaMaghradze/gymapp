package com.rest.gymapp.repository;

import com.rest.gymapp.model.Trainee;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    @EntityGraph(attributePaths = {"user", "trainers", "trainings"})
    Optional<Trainee> findByUserUsername(String username);
}
