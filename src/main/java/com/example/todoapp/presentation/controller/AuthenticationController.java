package com.example.todoapp.presentation.controller;

import com.example.todoapp.application.service.AuthenticationService;
import com.example.todoapp.application.service.UserContextService;
import com.example.todoapp.domain.model.User;
import com.example.todoapp.presentation.dto.request.LoginRequest;
import com.example.todoapp.presentation.dto.request.RegisterRequest;
import com.example.todoapp.presentation.dto.response.AuthenticationResponse;
import com.example.todoapp.presentation.dto.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserContextService userContextService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthenticationResponse response = authenticationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthenticationResponse response = authenticationService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        User currentUser = userContextService.getCurrentUser();
        
        UserResponse userResponse = new UserResponse(
                currentUser.getId(),
                currentUser.getUsername(),
                currentUser.getEmail(),
                currentUser.getCreatedAt(),
                currentUser.getUpdatedAt()
        );
        
        return ResponseEntity.ok(userResponse);
    }
}