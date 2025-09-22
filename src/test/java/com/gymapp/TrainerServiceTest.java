package com.gymapp;

import com.gymapp.dto.request.trainer.TrainerActivationRequest;
import com.gymapp.dto.request.trainer.TrainerRegistrationRequest;
import com.gymapp.dto.request.trainer.TrainerUpdateRequest;
import com.gymapp.dto.response.RegistrationResponse;
import com.gymapp.dto.response.trainer.TrainerProfileResponse;
import com.gymapp.dto.response.trainer.TrainerUpdateResponse;
import com.gymapp.dto.response.training.TrainingResponseForTrainer;
import com.gymapp.model.*;
import com.gymapp.repository.TrainerRepository;
import com.gymapp.repository.TrainingTypeRepository;
import com.gymapp.repository.UserRepository;
import com.gymapp.service.impl.TrainerServiceImpl;
import com.gymapp.utils.CredentialsGenerator;
import com.gymapp.utils.Mappers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CredentialsGenerator credentialsGenerator;

    @Mock
    private Mappers mappers;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private final String transactionId = UUID.randomUUID().toString();

    @Test
    void createTrainerProfile_ShouldCreateAndReturnCredentials() {

        TrainerRegistrationRequest request = new TrainerRegistrationRequest("John", "Doe", "Fitness");

        TrainingType trainingType = new TrainingType();
        when(trainingTypeRepository.findByTrainingTypeName("Fitness"))
                .thenReturn(Optional.of(trainingType));

        when(credentialsGenerator.generateUsername(anyString(), anyString(), any()))
                .thenReturn("jdoe");
        when(credentialsGenerator.generatePassword())
                .thenReturn("pass123");

        User savedUser = new User();
        savedUser.setUsername("jdoe");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        when(trainerRepository.save(any(Trainer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RegistrationResponse response = trainerService.createTrainerProfile(request, transactionId);

        assertEquals("jdoe", response.username());
        assertEquals("pass123", response.password());
        verify(userRepository).save(any(User.class));
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void getTrainerByUsername_ShouldReturnProfileResponse() {
        // given
        String username = "trainer1";
        String password = "pwd";

        Trainer trainer = new Trainer();
        when(trainerRepository.findByUserUsername(username))
                .thenReturn(Optional.of(trainer));

        TrainerProfileResponse mockResponse = new TrainerProfileResponse("John", "Doe", null, true);
        when(mappers.getTrainerProfileResponse(trainer)).thenReturn(mockResponse);

        // when
        TrainerProfileResponse result = trainerService.getTrainerByUsername(username, password, transactionId);

        // then
        assertEquals("John", result.getFirstName());
    }

    @Test
    void updateTrainerProfile_ShouldUpdateNamesAndReturnResponse() {
        // given
        String username = "trainer1";
        String password = "pwd";
        String transactionId = "tx-123";

        User user = new User();
        user.setFirstName("Old");
        user.setLastName("Name");
        user.setUsername("oldname");

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        // repo mock
        when(trainerRepository.findByUserUsername(username))
                .thenReturn(Optional.of(trainer));

        // username generator mock
        when(credentialsGenerator.generateUsername(anyString(), anyString(), any()))
                .thenReturn("newname");

        // trainingType mock (not used in this case, but service tries to check if null)
        when(trainingTypeRepository.findByTrainingTypeName(anyString()))
                .thenReturn(Optional.of(new TrainingType()));

        // request
        TrainerUpdateRequest request = new TrainerUpdateRequest("New", "Name", null, true);

        TrainerUpdateResponse updateResponse =
                new TrainerUpdateResponse("newname", "New", "Name", null, true);

        // fix → match any non-null trainer
        when(mappers.getTrainerUpdateResponse(any(Trainer.class))).thenReturn(updateResponse);

        // when
        TrainerUpdateResponse response =
                trainerService.updateTrainerProfile(request, username, password, transactionId);

        // then
        assertEquals("newname", response.getUsername());
        assertEquals("New", response.getFirstName());

        verify(userRepository).save(user);
        verify(trainerRepository).save(trainer);
        verify(mappers).getTrainerUpdateResponse(any(Trainer.class));
    }

    @Test
    void activateDeactivateTrainer_ShouldUpdateStatus() {
        // given
        String username = "trainer1";
        String password = "pwd";

        User user = new User();
        user.setIsActive(false);

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        when(trainerRepository.findByUserUsername(username)).thenReturn(Optional.of(trainer));

        TrainerActivationRequest req = new TrainerActivationRequest(username, true);

        // when
        trainerService.activateDeactivateTrainer(req, username, password, transactionId);

        // then
        assertTrue(user.getIsActive());
        verify(userRepository).save(user);
    }

    @Test
    void findTrainerTrainingsByCriteria_ShouldReturnFilteredList() {
        // given
        String username = "john.smith";
        String password = "pwd";

        User traineeUser = new User();
        traineeUser.setFirstName("John");
        traineeUser.setLastName("Smith");

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);

        TrainingType trainingType = new TrainingType("YOGA");

        Training training = new Training();
        training.setTrainingType(trainingType);
        training.setTrainingDate(LocalDate.now());
        training.setTrainee(trainee);

        Trainer trainer = new Trainer();
        trainer.setTrainings(Set.of(training));

        when(trainerRepository.findByUserUsername(username)).thenReturn(Optional.of(trainer));

        TrainingResponseForTrainer mapped = new TrainingResponseForTrainer(trainingType.getTrainingTypeName(),
                LocalDate.now(),
                mappers.getTrainingTypeResponse(trainingType),
                10,
                "john.smith");

        when(mappers.getTrainingResponseForTrainer(training)).thenReturn(mapped);

        // when
        List<TrainingResponseForTrainer> result =
                trainerService.findTrainerTrainingsByCriteria(username, password, null, null, "john", transactionId);

        // then
        assertEquals(1, result.size());
        assertEquals("john.smith", result.getFirst().getTraineeName());
    }
}
