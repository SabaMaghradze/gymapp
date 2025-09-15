package com.rest.gymapp.dto.request.trainee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TraineeActivationRequest(
        @NotBlank String username,
        @NotNull Boolean isActive
) {
}
