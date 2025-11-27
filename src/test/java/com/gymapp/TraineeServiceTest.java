package com.gymapp;

import com.gymapp.dto.request.trainee.TraineeActivationRequest;
import com.gymapp.dto.request.trainee.TraineeRegistrationRequest;
import com.gymapp.dto.request.trainee.TraineeUpdateRequest;
import com.gymapp.dto.request.trainee.UpdateTraineeTrainersRequest;
import com.gymapp.dto.request.trainer.TrainerRequestForTraineeTrainerListUpdate;
import com.gymapp.dto.response.RegistrationResponse;
import com.gymapp.dto.response.trainee.TraineeProfileResponse;
import com.gymapp.dto.response.trainee.TraineeUpdateResponse;
import com.gymapp.dto.response.trainer.TrainerResponseBasic;
import com.gymapp.exception.role.RoleNotFoundException;
import com.gymapp.model.Role;
import com.gymapp.model.Trainee;
import com.gymapp.model.Trainer;
import com.gymapp.model.User;
import com.gymapp.exception.user.UserNotFoundException;
import com.gymapp.monitoring.metrics.TraineeMetrics;
import com.gymapp.repository.RoleRepository;
import com.gymapp.repository.TraineeRepository;
import com.gymapp.repository.TrainerRepository;
import com.gymapp.repository.UserRepository;
import com.gymapp.service.impl.TraineeServiceImpl;
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

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private CredentialsGenerator credentialsGenerator;

    @Mock
    private Mappers mappers;

    @Mock
    private TraineeMetrics traineeMetrics;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private final String transactionId = "tx-123";

    private User user;

    private Trainee trainee;

    private Role userRole;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("john.doe");
        user.setPassword("encodedPassword");
        user.setIsActive(true);
        user.setIsEnabled(true);
        user.setAccNonLocked(true);
        user.setNumberOfFailedAttempts(0);

        trainee = new Trainee();
        trainee.setUser(user);

        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(user);
        trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
        trainee.setAddress("Tbilisi");

        userRole = new Role();
        userRole.setName("ROLE_USER");
    }


    @Test
    void createTraineeProfile_success() {

        TraineeRegistrationRequest req = new TraineeRegistrationRequest(
                "John", "Doe", "password123", LocalDate.of(2001, 1, 1), "Tbilisi"
        );

        when(credentialsGenerator.generateUsername("John", "Doe", userRepository))
                .thenReturn("john.doe");
        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword123");
        when(roleRepository.findByName("ROLE_USER"))
                .thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        when(traineeRepository.save(any(Trainee.class)))
                .thenReturn(trainee);

        RegistrationResponse response = traineeService.createTraineeProfile(req, transactionId);

        assertThat(response.username()).isEqualTo("john.doe");
        assertThat(response.password()).isEqualTo("password123");

        verify(userRepository).save(argThat(user ->
                user.getFirstName().equals("John") &&
                        user.getLastName().equals("Doe") &&
                        user.getUsername().equals("john.doe") &&
                        user.getPassword().equals("encodedPassword123")
        ));
        verify(traineeRepository).save(any(Trainee.class));
        verify(traineeMetrics).incrementTraineesCreated();
    }

    @Test
    void createTraineeProfile_roleNotFound_throwsException() {

        TraineeRegistrationRequest req = new TraineeRegistrationRequest(
                "John", "Doe", "password123", null, null
        );

        when(credentialsGenerator.generateUsername(anyString(), anyString(), any()))
                .thenReturn("john.doe");
        when(roleRepository.findByName("ROLE_USER"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeService.createTraineeProfile(req, transactionId))
                .isInstanceOf(RoleNotFoundException.class)
                .hasMessage("Role ROLE_USER not found.");

        verify(userRepository, never()).save(any());
        verify(traineeRepository, never()).save(any());
    }

    @Test
    void getTraineeById_success() {

        Long traineeId = 1L;
        TraineeProfileResponse expectedResponse = new TraineeProfileResponse(
                "John", "Doe", LocalDate.of(2000, 1, 1), "Tbilisi", true
        );

        when(traineeRepository.findById(traineeId))
                .thenReturn(Optional.of(trainee));
        when(mappers.getTraineeProfileResponse(trainee))
                .thenReturn(expectedResponse);

        TraineeProfileResponse response = traineeService.getTraineeById(traineeId, transactionId);

        assertThat(response).isEqualTo(expectedResponse);
        verify(traineeRepository).findById(traineeId);
    }

    @Test
    void activateDeactivateTrainee_success() {

        Long traineeId = 1L;
        TraineeActivationRequest req = new TraineeActivationRequest("john.doe", false);

        when(traineeRepository.findById(traineeId))
                .thenReturn(Optional.of(trainee));
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        traineeService.activateDeactivateTrainee(req, traineeId, transactionId);

        verify(userRepository).save(argThat(savedUser ->
                savedUser.getIsActive() == false
        ));
    }

    @Test
    void activateDeactivateTrainee_alreadyInDesiredState_throwsConflict() {
        // Arrange
        Long traineeId = 1L;
        user.setIsActive(false);
        TraineeActivationRequest req = new TraineeActivationRequest("john.doe", false);

        when(traineeRepository.findById(traineeId))
                .thenReturn(Optional.of(trainee));

        assertThatThrownBy(() ->
                traineeService.activateDeactivateTrainee(req, traineeId, transactionId))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.CONFLICT);

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateTraineeProfile_withNameChange_generatesNewUsername() {

        Long traineeId = 1L;
        TraineeUpdateRequest req = new TraineeUpdateRequest(
                "John", "Smith", LocalDate.of(2000, 1, 1), "Tbilisi", true
        );

        when(traineeRepository.findById(traineeId))
                .thenReturn(Optional.of(trainee));
        when(credentialsGenerator.generateUsername("John", "Smith", userRepository))
                .thenReturn("john.smith");
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        when(traineeRepository.save(any(Trainee.class)))
                .thenReturn(trainee);

        TraineeUpdateResponse expectedResponse = new TraineeUpdateResponse(
                "john.smith", "John", "Smith", true, "Tbilisi"
        );
        when(mappers.getTraineeUpdateResponse(any(Trainee.class)))
                .thenReturn(expectedResponse);

        TraineeUpdateResponse response = traineeService.updateTraineeProfile(req, traineeId, transactionId);

        assertThat(response.getUsername()).isEqualTo("john.smith");
        verify(credentialsGenerator).generateUsername("John", "Smith", userRepository);
    }

    @Test
    void deleteTraineeProfile_success() {

        Long traineeId = 1L;
        user.setRoles(new ArrayList<>());

        when(traineeRepository.findById(traineeId))
                .thenReturn(Optional.of(trainee));

        traineeService.deleteTraineeProfile(traineeId, transactionId);

        verify(traineeRepository).delete(trainee);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteTraineeProfile_noAssociatedUser_throwsException() {
        // Arrange
        Long traineeId = 1L;
        trainee.setUser(null);

        when(traineeRepository.findById(traineeId))
                .thenReturn(Optional.of(trainee));

        assertThatThrownBy(() -> traineeService.deleteTraineeProfile(traineeId, transactionId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("No associated user with this trainee.");
    }

    @Test
    void findNonAssignedTrainers_whenNoTrainersAssigned_returnsAllTrainers() {

        Long traineeId = 1L;
        trainee.setTrainers(new HashSet<>());

        Trainer trainer1 = createTrainer("trainer1", 1L);
        Trainer trainer2 = createTrainer("trainer2", 2L);

        when(traineeRepository.findById(traineeId))
                .thenReturn(Optional.of(trainee));
        when(trainerRepository.findAll())
                .thenReturn(Arrays.asList(trainer1, trainer2));
        when(mappers.getTrainerResponseBasic(trainer1))
                .thenReturn(new TrainerResponseBasic("trainer1"));
        when(mappers.getTrainerResponseBasic(trainer2))
                .thenReturn(new TrainerResponseBasic("trainer2"));

        List<TrainerResponseBasic> result = traineeService.findNonAssignedTrainers(traineeId, transactionId);

        assertThat(result).hasSize(2);
        assertThat(result).extracting("username").containsExactly("trainer1", "trainer2");
    }

    private Trainer createTrainer(String username, Long userId) {
        User trainerUser = new User();
        trainerUser.setId(userId);
        trainerUser.setUsername(username);

        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setTrainees(new HashSet<>());

        return trainer;
    }
}