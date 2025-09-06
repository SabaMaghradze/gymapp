package com.rest.gymapp.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordChangeRequest(
        @NotBlank(message = "Username is required") String username,
        @NotBlank(message = "Enter old password") String oldPassword,
        @NotBlank(message = "Enter new password") String newPassword
) {}
