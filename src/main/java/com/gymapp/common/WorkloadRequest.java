package com.gymapp.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WorkloadRequest implements Serializable {

    private String trainerUsername;

    private String trainerFirstName;

    private String trainerLastName;

    private boolean isActive;

    private LocalDate trainingDate;

    private Integer duration;

    private String actionType;
}
