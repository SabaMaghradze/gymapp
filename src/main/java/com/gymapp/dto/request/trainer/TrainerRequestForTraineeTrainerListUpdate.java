package com.gymapp.dto.request.trainer;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TrainerRequestForTraineeTrainerListUpdate {
        @NotBlank String username;
}
