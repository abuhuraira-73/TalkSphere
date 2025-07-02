package com.chatapp.chatappbackend.controller;

import com.chatapp.chatappbackend.dto.AuthResponse;
import com.chatapp.chatappbackend.dto.LoginRequest;
import com.chatapp.chatappbackend.dto.RegisterRequest;
import com.chatapp.chatappbackend.model.User;
import com.chatapp.chatappbackend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            User registeredUser = authService.registerUser(
                    registerRequest.getUsername(),
                    registerRequest.getEmail(),
                    registerRequest.getPassword()
            );
            
            AuthResponse response = new AuthResponse(
                    "User registered successfully",
                    true,
                    registeredUser.getId(),
                    registeredUser.getUsername(),
                    registeredUser.getEmail(),
                    registeredUser.getDisplayName(),
                    registeredUser.getAbout(),
                    registeredUser.getProfilePictureUrl()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            AuthResponse response = new AuthResponse(
                    e.getMessage(),
                    false,
                    null,
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            AuthResponse response = new AuthResponse(
                    "An unexpected error occurred",
                    false,
                    null,
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            User authenticatedUser = authService.authenticateUser(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );
            
            AuthResponse response = new AuthResponse(
                    "Login successful",
                    true,
                    authenticatedUser.getId(),
                    authenticatedUser.getUsername(),
                    authenticatedUser.getEmail(),
                    authenticatedUser.getDisplayName(),
                    authenticatedUser.getAbout(),
                    authenticatedUser.getProfilePictureUrl()
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            AuthResponse response = new AuthResponse(
                    e.getMessage(),
                    false,
                    null,
                    null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            AuthResponse response = new AuthResponse(
                    "An unexpected error occurred",
                    false,
                    null,
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/user-by-username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            User user = authService.getUserByUsername(username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("displayName", user.getDisplayName());
            response.put("about", user.getAbout());
            response.put("profilePictureUrl", user.getProfilePictureUrl());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}