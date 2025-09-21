package com.gymapp.dto.request.trainer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrainerActivationRequest(
        @NotBlank String username,
        @NotNull Boolean isActive
) {
}
