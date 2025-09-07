package com.rest.gymapp.dto.request.trainer;

import com.rest.gymapp.model.TrainingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.ReadOnlyProperty;


public record TrainerUpdateRequest(
        @NotBlank String username,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @ReadOnlyProperty TrainingType specialization,
        @NotNull Boolean isActive
) {
}
