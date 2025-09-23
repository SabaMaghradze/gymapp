package com.gymapp.service.impl;

import com.gymapp.dto.request.auth.LoginRequest;
import com.gymapp.exception.AuthenticationException;
import com.gymapp.exception.BadCredentialsException;
import com.gymapp.exception.LockedException;
import com.gymapp.model.User;
import com.gymapp.security.jwt.JwtUtil;
import com.gymapp.security.user.UserDetailsCustom;
import com.gymapp.service.AuthService;
import com.gymapp.service.UserService;
import com.gymapp.utils.AppContants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public void authenticateUser(LoginRequest loginRequest) {

        String username = loginRequest.username();
        User user = userService.getUserByUsername(username);

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateToken(authentication);

            UserDetailsCustom userDetailsCustom = (UserDetailsCustom) authentication.getPrincipal();

            List<String> roles = userDetailsCustom.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            userService.resetFailedAttempts(user);

        } catch (AuthenticationException exc) {

            if (user.getIsEnabled()) {
                if (user.getAccNonLocked()) {
                    if (user.getNumberOfFailedAttempts() < AppContants.ATTEMPT_COUNT) {
                        userService.increaseFailedAttempts(user);
                        throw new BadCredentialsException("Incorrect Credentials, Please Try Again");
                    } else {
                        userService.lockAccount(user);
                        throw new LockedException("Your account has been locked, failed attempt N.3");
                    }
                } else {
                    if (userService.unlockAcc(user)) {
                        throw new LockedException("Your account is unlocked, please try again.");
                    } else {
                        throw new LockedException("Your account is locked, please try again later");
                    }
                }
            } else {
                throw new LockedException("Your account is inactive");
            }
        }
    }
}
