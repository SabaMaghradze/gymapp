package com.rest.gymapp.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record PasswordChangeRequest(
        @NotBlank(message = "Enter old password") String oldPassword,
        @NotBlank(message = "Enter new password") String newPassword
) {}
