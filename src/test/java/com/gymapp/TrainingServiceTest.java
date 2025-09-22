package com.gymapp;

import com.gymapp.model.*;
import com.gymapp.repository.TraineeRepository;
import com.gymapp.repository.TrainerRepository;
import com.gymapp.repository.TrainingRepository;
import com.gymapp.repository.TrainingTypeRepository;
import com.gymapp.dto.request.training.TrainingRegistrationRequest;
import com.gymapp.exception.ResourceNotFoundException;
import com.gymapp.exception.UserNotFoundException;
import com.gymapp.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private TrainingRegistrationRequest request;
    private Trainee trainee;
    private Trainer trainer;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        request = new TrainingRegistrationRequest(
                "traineeUser",
                "trainerUser",
                "Yoga",
                LocalDate.now(),
                60
        );

        trainee = new Trainee();
        User traineeUser = new User();
        traineeUser.setUsername("traineeUser");
        trainee.setUser(traineeUser);

        trainer = new Trainer();
        User trainerUser = new User();
        trainerUser.setUsername("trainerUser");
        trainer.setUser(trainerUser);

        trainingType = new TrainingType();
        trainingType.setTrainingTypeName("Yoga");
        trainer.setSpecialization(trainingType);
    }

    @Test
    void addTraining_success() {
        when(traineeRepository.findByUserUsername("traineeUser")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUsername("trainerUser")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName("Yoga")).thenReturn(Optional.of(trainingType));
        when(trainingRepository.save(any(Training.class))).thenAnswer(invocation -> {
            Training saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        trainingService.addTraining(request, "traineeUser", "password", "tx123");

        verify(trainingRepository).save(any(Training.class));
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void addTraining_failsWhenTraineeNotFound() {
        when(traineeRepository.findByUserUsername("traineeUser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                trainingService.addTraining(request, "traineeUser", "password", "tx123")
        );
    }

    @Test
    void addTraining_failsWhenTrainerNotFound() {
        when(traineeRepository.findByUserUsername("traineeUser")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUsername("trainerUser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                trainingService.addTraining(request, "traineeUser", "password", "tx123")
        );
    }

    @Test
    void addTraining_failsWhenTrainingTypeNotFound() {
        when(traineeRepository.findByUserUsername("traineeUser")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUsername("trainerUser")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName("Yoga")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                trainingService.addTraining(request, "traineeUser", "password", "tx123")
        );
    }

    @Test
    void addTraining_failsWhenTrainerSpecializationMismatch() {
        TrainingType wrongType = new TrainingType();
        wrongType.setTrainingTypeName("Pilates");
        trainer.setSpecialization(wrongType);

        when(traineeRepository.findByUserUsername("traineeUser")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUsername("trainerUser")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName("Yoga")).thenReturn(Optional.of(trainingType));

        assertThrows(ResourceNotFoundException.class, () ->
                trainingService.addTraining(request, "traineeUser", "password", "tx123")
        );
    }
}
