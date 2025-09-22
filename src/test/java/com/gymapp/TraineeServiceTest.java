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
import com.gymapp.model.Trainee;
import com.gymapp.model.Trainer;
import com.gymapp.model.User;
import com.gymapp.exception.UserNotFoundException;
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
    private CredentialsGenerator credentialsGenerator;

    @Mock
    private Mappers mappers;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private final String transactionId = "tx-123";

    private User user;
    private Trainee trainee;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("john.doe");
        user.setPassword("pass");
        user.setIsActive(true);

        trainee = new Trainee();
        trainee.setUser(user);
    }


    @Test
    void createTraineeProfile_success() {
        TraineeRegistrationRequest req = new TraineeRegistrationRequest("John", "Doe", LocalDate.of(2000, 1, 1), "Tbilisi");

        when(credentialsGenerator.generateUsername(any(), any(), any())).thenReturn("john.doe");
        when(credentialsGenerator.generatePassword()).thenReturn("pwd123");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);

        RegistrationResponse response = traineeService.createTraineeProfile(req, transactionId);

        assertThat(response.username()).isEqualTo("john.doe");
        assertThat(response.password()).isEqualTo("pwd123");
        verify(userRepository).save(any(User.class));
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void getTraineeProfileByUsername_success() {
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(mappers.getTraineeProfileResponse(any(Trainee.class)))
                .thenReturn(new TraineeProfileResponse("John", "Doe", LocalDate.of(2000, 1, 1), "Tbilisi", true));

        TraineeProfileResponse response = traineeService.getTraineeProfileByUsername("john.doe", "pass", transactionId);

        assertThat(response.getFirstName()).isEqualTo("John");
    }

    @Test
    void getTraineeProfileByUsername_notFound() {
        when(traineeRepository.findByUserUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeService.getTraineeProfileByUsername("unknown", "pass", transactionId))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void activateDeactivateTrainee_success() {
        TraineeActivationRequest req = new TraineeActivationRequest("john.doe", false);
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));

        traineeService.activateDeactivateTrainee(req, "john.doe", "pass", transactionId);

        verify(userRepository).save(any(User.class));
        assertThat(user.getIsActive()).isFalse();
    }

    @Test
    void updateTraineeProfile_success() {
        TraineeUpdateRequest req = new TraineeUpdateRequest("John", "Smith", LocalDate.of(2000, 1, 1), "Tbilisi", true);

        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(credentialsGenerator.generateUsername(any(), any(), any())).thenReturn("john.smith");
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);
        when(mappers.getTraineeUpdateResponse(any(Trainee.class)))
                .thenReturn(new TraineeUpdateResponse("john.smith", "John", "Smith", true, "Tbilisi"));

        TraineeUpdateResponse response = traineeService.updateTraineeProfile(req, "john.doe", "pass", transactionId);

        assertThat(response.getUsername()).isEqualTo("john.smith");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteTraineeProfile_success() {
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));

        traineeService.deleteTraineeProfile("john.doe", "pass", transactionId);

        verify(traineeRepository).delete(trainee);
    }

    @Test
    void findNonAssignedTrainers_returnsList() {
        Trainer trainer = new Trainer();
        User trainerUser = new User();
        trainerUser.setUsername("trainer1");
        trainer.setUser(trainerUser);
        trainer.setTrainees(new HashSet<>());

        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findAll()).thenReturn(List.of(trainer));
        when(mappers.getTrainerResponseBasic(any(Trainer.class)))
                .thenReturn(new TrainerResponseBasic("trainer1"));

        List<TrainerResponseBasic> result = traineeService.findNonAssignedTrainers("johndoe", "pass", transactionId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getUsername()).isEqualTo("trainer1");
    }

    @Test
    void updateTraineeTrainers_addsNewTrainer() {
        TrainerRequestForTraineeTrainerListUpdate trainerReq = new TrainerRequestForTraineeTrainerListUpdate("trainer1");

        Trainer trainer = new Trainer();
        User trainerUser = new User();
        trainerUser.setUsername("trainer1");
        trainer.setUser(trainerUser);

        trainee.setTrainers(new HashSet<>());

        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUsername("trainer1")).thenReturn(Optional.of(trainer));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);
        when(mappers.getTrainerResponseBasic(any(Trainer.class)))
                .thenReturn(new TrainerResponseBasic("trainer1"));

        UpdateTraineeTrainersRequest req = new UpdateTraineeTrainersRequest(List.of(trainerReq));

        List<TrainerResponseBasic> result = traineeService.updateTraineeTrainers(req, "john.doe", "pass", transactionId);

        assertThat(result).hasSize(1);
        verify(traineeRepository).save(trainee);
    }
}