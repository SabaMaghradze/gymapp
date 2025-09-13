package com.rest.gymapp.dto.request.trainee;

import jakarta.validation.constraints.NotNull;

public record TraineeActivationRequest(
        @NotNull Boolean isActive
) {
}
