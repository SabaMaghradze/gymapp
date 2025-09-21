package com.gymapp.dto.request.trainee;

import com.gymapp.dto.request.trainer.TrainerRequestForTraineeTrainerListUpdate;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UpdateTraineeTrainersRequest {
        @NotEmpty List<TrainerRequestForTraineeTrainerListUpdate> trainers;
}
