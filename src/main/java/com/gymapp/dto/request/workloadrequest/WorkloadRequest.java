package com.gymapp.dto.request.workloadrequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WorkloadRequest {

    private String trainerUsername;

    private String trainerFirstName;

    private String trainerLastName;

    private boolean isActive;

    private LocalDate trainingDate;

    private Integer duration;

    private String actionType;
}
