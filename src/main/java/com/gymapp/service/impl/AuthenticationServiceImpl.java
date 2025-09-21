package com.gymapp.service.impl;

import com.gymapp.exception.InvalidCredentialsException;
import com.gymapp.exception.ResourceNotFoundException;
import com.gymapp.exception.UserInactiveException;
import com.gymapp.exception.UserNotFoundException;
import com.gymapp.model.User;
import com.gymapp.repository.TraineeRepository;
import com.gymapp.repository.TrainerRepository;
import com.gymapp.repository.UserRepository;
import com.gymapp.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    private final UserRepository userRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public void authenticateTrainee(String username, String password) {

        logger.info("Authenticating trainee: {}", username);

        authenticateUser(username, password);

        traineeRepository.findByUserUsername(username)
                        .orElseThrow(() -> new ResourceNotFoundException("The user is not a trainee"));
    }

    public void authenticateTrainer(String username, String password) {

        logger.info("Authenticating trainer: {}", username);

        authenticateUser(username, password);

        trainerRepository.findByUserUsername(username)
                        .orElseThrow(() -> new InvalidCredentialsException("The user is not a trainer"));
    }

    public void authenticateUser(String username, String password) {

        if (username.isEmpty() || password.isEmpty()) {
            throw new InvalidCredentialsException("Please provide username and password");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!user.getIsActive()) {
            logger.warn("Authentication failed: User {} is deactivated", username);
            throw new UserInactiveException("The user is inactive");
        }

        if (!user.getPassword().equals(password)) {
            logger.warn("Authentication failed: Wrong password for user {}", username);
            throw new InvalidCredentialsException("Invalid username or password");
        }

        logger.info("User {} successfully authenticated", username);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {

        logger.info("Changing password for user: {}", username);

        authenticateUser(username, oldPassword);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.getPassword().equals(oldPassword)) {
            throw new InvalidCredentialsException("Wrong password, please try again");
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new RuntimeException("New password cannot be empty");
        }

        user.setPassword(newPassword);
    }
}
