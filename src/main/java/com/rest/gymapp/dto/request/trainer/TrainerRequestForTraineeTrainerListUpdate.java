package com.rest.gymapp.dto.request.trainer;

import jakarta.validation.constraints.NotBlank;

public record TrainerRequestForTraineeTrainerListUpdate(
        @NotBlank String username
) {
}
