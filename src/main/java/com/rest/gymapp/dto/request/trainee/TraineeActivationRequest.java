package com.rest.gymapp.dto.request.trainee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record TraineeActivationRequest(
        @NotBlank String username,
        @NotEmpty Boolean isActive
) {
}
