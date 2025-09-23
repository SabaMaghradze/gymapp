package com.gymapp.service;

import com.gymapp.model.User;

public interface UserService {

    User getUserByUsername(String username);

    User getUserById(Long id);

    void updateResetToken(String username, String resetToken);

    User getUserByResetToken(String token);

    void increaseFailedAttempts(User user);

    void lockAccount(User user);

    Boolean unlockAcc(User user);

    void resetFailedAttempts(User user);
}
