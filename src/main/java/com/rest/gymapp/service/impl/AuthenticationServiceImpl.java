package com.rest.gymapp.service.impl;

import com.rest.gymapp.exception.InvalidCredentialsException;
import com.rest.gymapp.exception.ResourceNotFoundException;
import com.rest.gymapp.exception.UserNotFoundException;
import com.rest.gymapp.model.User;
import com.rest.gymapp.repository.TraineeRepository;
import com.rest.gymapp.repository.TrainerRepository;
import com.rest.gymapp.repository.UserRepository;
import com.rest.gymapp.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
            throw new InvalidCredentialsException("Invalid username or password");
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

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("Failed to find user " + username);
        }

        User user = userOpt.get();

        if (!user.getPassword().equals(oldPassword)) {
            throw new InvalidCredentialsException("Wrong password, please try again");
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new RuntimeException("New password cannot be empty");
        }

        user.setPassword(newPassword);
    }
}
