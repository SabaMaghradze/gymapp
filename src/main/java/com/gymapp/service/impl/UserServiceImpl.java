package com.gymapp.service.impl;

import com.gymapp.exception.user.UserNotFoundException;
import com.gymapp.model.User;
import com.gymapp.repository.UserRepository;
import com.gymapp.service.UserService;
import com.gymapp.utils.AppContants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public void updateResetToken(String username, String resetToken) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(1));
        userRepository.save(user);
    }

    @Override
    public User getUserByResetToken(String token) {
        return userRepository.findByResetToken(token)
                .orElseThrow(() -> new UserNotFoundException("Token is invalid or has expired"));
    }

    @Override
    public void increaseFailedAttempts(User user) {
        user.setNumberOfFailedAttempts(user.getNumberOfFailedAttempts() + 1);
        userRepository.save(user);
    }

    @Override
    public void lockAccount(User user) {
        user.setAccNonLocked(false);
        user.setLockTime(new Date());
        userRepository.save(user);
    }

    @Override
    public Boolean unlockAcc(User user) {
        long lockTime = user.getLockTime().getTime();
        long unlockTime = lockTime + AppContants.UNLOCK_DURATION_TIME;

        long currentTime = System.currentTimeMillis();

        if (unlockTime < currentTime) {
            user.setAccNonLocked(true);
            user.setLockTime(null);
            user.setNumberOfFailedAttempts(0);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void resetFailedAttempts(User user) {
        user.setNumberOfFailedAttempts(null);
    }
}
