package com.gymapp;

import com.gymapp.dto.request.trainer.TrainerActivationRequest;
import com.gymapp.dto.request.trainer.TrainerRegistrationRequest;
import com.gymapp.dto.request.trainer.TrainerUpdateRequest;
import com.gymapp.dto.response.RegistrationResponse;
import com.gymapp.dto.response.trainer.TrainerProfileResponse;
import com.gymapp.dto.response.trainer.TrainerUpdateResponse;
import com.gymapp.dto.response.training.TrainingResponseForTrainer;
import com.gymapp.exception.resource.ResourceNotFoundException;
import com.gymapp.exception.user.UserNotFoundException;
import com.gymapp.model.*;
import com.gymapp.monitoring.metrics.TrainerMetrics;
import com.gymapp.repository.RoleRepository;
import com.gymapp.repository.TrainerRepository;
import com.gymapp.repository.TrainingTypeRepository;
import com.gymapp.repository.UserRepository;
import com.gymapp.service.impl.TrainerServiceImpl;
import com.gymapp.utils.CredentialsGenerator;
import com.gymapp.utils.Mappers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TrainerMetrics trainerMetrics;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Role userRole;

    private Trainer trainer;

    private User user;

    private final String transactionId = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("john.doe");
        user.setPassword("pass123");
        user.setIsActive(true);
        user.setIsEnabled(true);
        user.setAccNonLocked(true);
        user.setNumberOfFailedAttempts(0);

        trainer = new Trainer();
        trainer.setUser(user);

        trainer.setId(1L);

        userRole = new Role();
        userRole.setName("ROLE_USER");
    }

    @Test
    void createTrainerProfile_ShouldCreateAndReturnCredentials() {

        TrainingType trainingType = new TrainingType("fitness");

        TrainerRegistrationRequest req = new TrainerRegistrationRequest("John", "Doe", "pass123", "fitness");

        when(credentialsGenerator.generateUsername("John", "Doe", userRepository))
                .thenReturn("john.doe");
        when(trainingTypeRepository.findByTrainingTypeName("fitness"))
                .thenReturn(Optional.of(trainingType));
        when(passwordEncoder.encode("pass123"))
                .thenReturn("encodedPass123");
        when(roleRepository.findByName("ROLE_USER"))
                .thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        when(trainerRepository.save(any(Trainer.class)))
                .thenReturn(trainer);

        RegistrationResponse response = trainerService.createTrainerProfile(req, transactionId);

        assertThat(response.username()).isEqualTo("john.doe");
        assertThat(response.password()).isEqualTo("pass123");

        verify(userRepository).save(argThat(user ->
                user.getFirstName().equals("John") &&
                        user.getLastName().equals("Doe") &&
                        user.getUsername().equals("john.doe") &&
                        user.getPassword().equals("encodedPass123")
        ));
        verify(trainerRepository).save(any(Trainer.class));
        verify(trainerMetrics).incrementTrainersCreated();
    }

    @Test
    void createTrainerProfile_WhenTrainingTypeNotFound_ShouldThrowException() {
        // Arrange
        TrainerRegistrationRequest req = new TrainerRegistrationRequest("john", "doe", "pass123", "unknown");

        when(trainingTypeRepository.findByTrainingTypeName("unknown"))
                .thenReturn(Optional.empty());  // Training type not found!

        // Act & Assert
        assertThatThrownBy(() -> trainerService.createTrainerProfile(req, transactionId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Training type not found");

        // Verify nothing was saved
        verify(userRepository, never()).save(any());
        verify(trainerRepository, never()).save(any());
    }

    @Test
    void getTrainerByUsername_ShouldReturnProfileResponse() {

        when(trainerRepository.findById(1L))
                .thenReturn(Optional.of(trainer));

        TrainerProfileResponse mockResponse = new TrainerProfileResponse("John", "Doe", null, true);
        when(mappers.getTrainerProfileResponse(trainer)).thenReturn(mockResponse);

        // when
        TrainerProfileResponse result = trainerService.getTrainerById(1L, transactionId);
        // then
        assertEquals("John", result.getFirstName());
    }

    @Test
    void updateTrainerProfile_ShouldUpdateNamesAndReturnResponse() {

        TrainerUpdateRequest request = new TrainerUpdateRequest("New", "Name", null, true);

        when(trainerRepository.findById(1L))
                .thenReturn(Optional.of(trainer));

        // Username will change because "New" != "John" and "Name" != "Doe"
        when(credentialsGenerator.generateUsername("New", "Name", userRepository))
                .thenReturn("new.name");

        // Mock the save operations to return the same objects (simulating DB behavior)
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);

        // Mock the mapper response
        TrainerUpdateResponse updateResponse =
                new TrainerUpdateResponse("new.name", "New", "Name", null, true);
        when(mappers.getTrainerUpdateResponse(trainer)).thenReturn(updateResponse);

        TrainerUpdateResponse response =
                trainerService.updateTrainerProfile(1L, request, transactionId);

        assertEquals("new.name", response.getUsername());
        assertEquals("New", response.getFirstName());
        assertEquals("Name", response.getLastName());

        verify(userRepository).save(argThat(u ->
                u.getFirstName().equals("New") &&
                        u.getLastName().equals("Name") &&
                        u.getUsername().equals("new.name") &&
                        u.getIsActive() == true
        ));
        verify(trainerRepository).save(trainer);
    }

    @Test
    void activateDeactivateTrainer_WhenDeactivating_ShouldSetInactive() {

        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(userRepository.save(any(User.class))).thenReturn(user);

        TrainerActivationRequest req = new TrainerActivationRequest("john.doe", false);

        trainerService.activateDeactivateTrainer(1L, req, transactionId);

        verify(userRepository).save(argThat(savedUser ->
                savedUser.getIsActive() == false
        ));
    }

    @Test
    void activateDeactivateTrainer_WhenActivating_ShouldSetActive() {

        user.setIsActive(false);
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(userRepository.save(any(User.class))).thenReturn(user);

        TrainerActivationRequest req = new TrainerActivationRequest("john.doe", true);

        trainerService.activateDeactivateTrainer(1L, req, transactionId);

        verify(userRepository).save(argThat(savedUser ->
                savedUser.getIsActive() == true
        ));
    }

    @Test
    void activateDeactivateTrainer_WhenAlreadyInDesiredState_ShouldThrowConflict() {

        user.setIsActive(true);
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        TrainerActivationRequest req = new TrainerActivationRequest("john.doe", true); // Request to activate (already active!)

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> trainerService.activateDeactivateTrainer(1L, req, transactionId)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertThat(exception.getReason()).contains("User is already active");

        verify(userRepository, never()).save(any());
    }

    @Test
    void activateDeactivateTrainer_WhenTrainerNotFound_ShouldThrowException() {

        when(trainerRepository.findById(999L)).thenReturn(Optional.empty());

        TrainerActivationRequest req = new TrainerActivationRequest("john.doe", false);

        assertThrows(
                UserNotFoundException.class,
                () -> trainerService.activateDeactivateTrainer(999L, req, transactionId)
        );

        verify(userRepository, never()).save(any());
    }
}
