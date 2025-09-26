package com.gymapp.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {

    private String username;

    private String token;

    private String type = "Bearer";

    private List<String> roles;

    public JwtResponse(String username, String jwt, List<String> roles) {
        this.username = username;
        this.token = jwt;
        this.roles = roles;
    }
}
