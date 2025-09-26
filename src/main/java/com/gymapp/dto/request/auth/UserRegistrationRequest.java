package com.gymapp.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record UserRegistrationRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String password
) {
}
