package com.rest.gymapp.dto.request.trainer;

import jakarta.validation.constraints.NotNull;

public record TrainerActivationRequest(
        @NotNull Boolean isActive
) {
}
