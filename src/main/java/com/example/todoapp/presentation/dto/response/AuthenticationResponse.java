package com.example.todoapp.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    
    private String accessToken;
    private String tokenType = "Bearer";
    private String email;
    private String firstName;
    private String lastName;

    public AuthenticationResponse(String accessToken, String email, String firstName, String lastName) {
        this.accessToken = accessToken;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}