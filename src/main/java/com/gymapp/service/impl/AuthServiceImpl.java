package com.gymapp.service.impl;

import com.gymapp.dto.request.auth.LoginRequest;
import com.gymapp.dto.request.auth.UserRegistrationRequest;
import com.gymapp.dto.response.auth.JwtResponse;
import com.gymapp.dto.response.auth.UserRegistrationResponse;
import com.gymapp.exception.auth.AuthenticationException;
import com.gymapp.exception.auth.BadCredentialsException;
import com.gymapp.exception.auth.LockedException;
import com.gymapp.exception.role.RoleNotFoundException;
import com.gymapp.exception.user.UserAlreadyExistsException;
import com.gymapp.model.BlacklistedToken;
import com.gymapp.model.Role;
import com.gymapp.model.User;
import com.gymapp.repository.BlacklistedTokenRepository;
import com.gymapp.repository.RoleRepository;
import com.gymapp.repository.UserRepository;
import com.gymapp.security.jwt.JwtUtil;
import com.gymapp.security.jwt.TokenHashUtil;
import com.gymapp.security.user.UserDetailsCustom;
import com.gymapp.service.AuthService;
import com.gymapp.service.UserService;
import com.gymapp.utils.AppContants;
import com.gymapp.utils.CredentialsGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CredentialsGenerator credentialsGenerator;
    private final UserRepository userRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {

        String username = loginRequest.username();
        User user = userService.getUserByUsername(username);

        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateToken(authentication);

            UserDetailsCustom userDetailsCustom = (UserDetailsCustom) authentication.getPrincipal();

            List<String> roles = userDetailsCustom.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            userService.resetFailedAttempts(user);

            return new JwtResponse(username, jwt, roles);

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

    @Override
    public UserRegistrationResponse registerUser(UserRegistrationRequest req) {

        String username = credentialsGenerator.generateUsername(req.firstName(), req.lastName(), userRepository);

        if (userService.existsByUsername(username)) {
            throw new UserAlreadyExistsException(username + " already exists");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RoleNotFoundException("Role ROLE_USER not found."));

        User user = new User();

        user.setFirstName(req.firstName());
        user.setLastName(req.lastName());
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(req.password()));

        user.setIsActive(true);
        user.setIsEnabled(true);
        user.setAccNonLocked(true);
        user.setNumberOfFailedAttempts(0);

        user.setRoles(Collections.singletonList(userRole));

        userRepository.save(user);

        return new UserRegistrationResponse(req.firstName(), req.lastName(), username);
    }
}
