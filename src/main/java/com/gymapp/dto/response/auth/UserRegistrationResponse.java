package com.gymapp.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserRegistrationResponse {

    private String firstName;

    private String lastName;

    private String username;
}
