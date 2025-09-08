package com.rest.gymapp.dto.request.trainee;

import com.rest.gymapp.dto.request.trainer.TrainerRequestForTraineeTrainerListUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UpdateTraineeTrainersRequest(
        @NotBlank String username,
        @NotEmpty List<TrainerRequestForTraineeTrainerListUpdate> trainers
) {
}
