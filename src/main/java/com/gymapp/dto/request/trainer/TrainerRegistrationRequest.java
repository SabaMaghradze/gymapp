package com.gymapp.dto.request.trainer;

import jakarta.validation.constraints.NotBlank;

public record TrainerRegistrationRequest(
        @NotBlank(message = "First name is a mandatory field") String firstName,
        @NotBlank(message = "Last name is a mandatory field") String lastName,
        @NotBlank(message = "Password is a mandatory field") String password,
        @NotBlank(message = "Please specify the training type") String specializationName
) {}
