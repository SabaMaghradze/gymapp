package com.rest.gymapp.dto.request.trainer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record TrainerActivationRequest(
        @NotBlank String username,
        @NotEmpty Boolean isActive
) {
}
