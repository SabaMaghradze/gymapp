package com.gymapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserResponse {

    private String firstName;

    private String lastName;

    private String username;

    private Boolean isActive;
}
