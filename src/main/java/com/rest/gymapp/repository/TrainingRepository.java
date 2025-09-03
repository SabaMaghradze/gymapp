package com.rest.gymapp.repository;

import com.rest.gymapp.model.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingRepository {

    Training save(Training training);

    Optional<Training> findById(Long id);

    List<Training> findAll();

    void delete(Training training);

}
