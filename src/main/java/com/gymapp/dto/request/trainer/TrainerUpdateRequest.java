package com.gymapp.dto.request.trainer;

import com.gymapp.model.TrainingType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrainerUpdateRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Nullable TrainingType trainingType,
        @NotNull Boolean isActive
) {
}
