package com.gymapp.dto.request.trainingType;

import jakarta.validation.constraints.NotBlank;

public record TrainingTypeRegistrationRequest(
        @NotBlank String trainingTypeName
) {
}
