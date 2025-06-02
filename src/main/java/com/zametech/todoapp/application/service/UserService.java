package com.zametech.todoapp.application.service;

import com.zametech.todoapp.common.exception.TodoNotFoundException;
import com.zametech.todoapp.domain.model.User;
import com.zametech.todoapp.domain.repository.TodoRepository;
import com.zametech.todoapp.domain.repository.UserRepository;
import com.zametech.todoapp.presentation.dto.request.ChangePasswordRequest;
import com.zametech.todoapp.presentation.dto.request.UpdateUserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserContextService userContextService;
    
    @Transactional
    public User updateUserProfile(Long userId, UpdateUserRequest request) {
        Long currentUserId = userContextService.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            throw new AccessDeniedException("You can only update your own profile");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TodoNotFoundException("User not found with id: " + userId));
        
        // Verify current password
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid current password");
        }
        
        // Update username if provided and different
        if (request.username() != null && !request.username().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.username())) {
                throw new IllegalArgumentException("Username already exists");
            }
            user.setUsername(request.username());
        }
        
        // Update email if provided and different
        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(request.email());
        }
        
        // Update password if provided
        if (request.newPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.newPassword()));
        }
        
        log.info("Updating user profile for userId: {}", userId);
        return userRepository.save(user);
    }
    
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        Long currentUserId = userContextService.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            throw new AccessDeniedException("You can only change your own password");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TodoNotFoundException("User not found with id: " + userId));
        
        // Verify current password
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid current password");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        
        log.info("Changing password for userId: {}", userId);
        userRepository.save(user);
    }
    
    @Transactional
    public void deleteUser(Long userId) {
        Long currentUserId = userContextService.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            throw new AccessDeniedException("You can only delete your own account");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TodoNotFoundException("User not found with id: " + userId));
        
        log.info("Deleting user account for userId: {}", userId);
        
        // Delete all user's todos first (cascade delete)
        todoRepository.deleteByUserId(userId);
        
        // Delete the user
        userRepository.deleteById(userId);
    }
    
    public User getUserById(Long userId) {
        Long currentUserId = userContextService.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            throw new AccessDeniedException("You can only view your own profile");
        }
        
        return userRepository.findById(userId)
                .orElseThrow(() -> new TodoNotFoundException("User not found with id: " + userId));
    }
}