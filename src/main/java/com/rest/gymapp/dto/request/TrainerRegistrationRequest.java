package com.rest.gymapp.dto.request;

import com.rest.gymapp.model.TrainingType;
import jakarta.validation.constraints.NotBlank;

public record TrainerRegistrationRequest(
        @NotBlank(message = "First name is mandatory") String firstName,
        @NotBlank(message = "Last name is mandatory") String lastName,
        @NotBlank(message = "Please specify the training type") TrainingType specialization
) {}
