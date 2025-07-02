package com.chatapp.chatappbackend.controller;

import com.chatapp.chatappbackend.dto.UpdateProfileRequest;
import com.chatapp.chatappbackend.dto.UserDetailsResponse;
import com.chatapp.chatappbackend.model.User;
import com.chatapp.chatappbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Controller for user-related endpoints
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Get user details by ID
    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailsResponse> getUserDetails(@PathVariable Integer userId) {
        try {
            User user = userService.getUserById(userId);
            
            UserDetailsResponse response = new UserDetailsResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName(),
                user.getAbout(),
                user.getProfilePictureUrl()
            );
            
            System.out.println("Returning user details for ID: " + userId + 
                             ", Username: " + user.getUsername() + 
                             ", Email: " + user.getEmail());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error fetching user details: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    // Get all users except current user
    @GetMapping("/all/{currentUserId}")
    public ResponseEntity<List<UserDetailsResponse>> getAllUsers(@PathVariable Integer currentUserId) {
        try {
            System.out.println("Fetching all users except user ID: " + currentUserId);
            List<User> users = userService.getAllUsersExceptCurrent(currentUserId);
            
            List<UserDetailsResponse> responseList = users.stream()
                .map(user -> new UserDetailsResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getDisplayName(),
                    user.getAbout(),
                    user.getProfilePictureUrl()
                ))
                .collect(Collectors.toList());
            
            System.out.println("Returning " + responseList.size() + " users");
            return ResponseEntity.ok(responseList);
        } catch (Exception e) {
            System.err.println("Error fetching all users: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    // Update profile picture
    @PostMapping("/{userId}/profile-picture")
    public ResponseEntity<UserDetailsResponse> updateProfilePicture(
            @PathVariable Integer userId,
            @RequestParam("file") MultipartFile file) {
        try {
            User user = userService.updateProfilePicture(userId, file);
            
            UserDetailsResponse response = new UserDetailsResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName(),
                user.getAbout(),
                user.getProfilePictureUrl()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error updating profile picture: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    // Update profile information
    @PutMapping("/{userId}/profile")
    public ResponseEntity<?> updateProfileInfo(
            @PathVariable Integer userId,
            @RequestBody UpdateProfileRequest request) {
        try {
            System.out.println("\n==== PROFILE UPDATE REQUEST START ====");
            System.out.println("Received profile update request for user ID: " + userId);
            
            // Validate user ID
            if (userId == null || userId <= 0) {
                System.err.println("Invalid user ID: " + userId);
                return ResponseEntity.badRequest().body(
                    new ErrorResponse("Invalid user ID. Must be a positive integer.")
                );
            }
            
            System.out.println("Request body: " + request);
            System.out.println("DisplayName: " + request.getDisplayName());
            System.out.println("About: " + request.getAbout());
            
            // Check if user exists before trying to update
            try {
                User checkUser = userService.getUserById(userId);
                System.out.println("Found user: " + checkUser.getUsername() + " (ID: " + checkUser.getId() + ")");
            } catch (Exception e) {
                System.err.println("Error finding user: " + e.getMessage());
                return ResponseEntity.badRequest().body(
                    new ErrorResponse("User not found with ID: " + userId)
                );
            }
            
            // Validate request data
            if (request.getDisplayName() == null && request.getAbout() == null) {
                System.err.println("Empty request: Both displayName and about are null");
                return ResponseEntity.badRequest().body(
                    new ErrorResponse("Request must include at least one field to update (displayName or about)")
                );
            }
            
            // Update the user profile
            User user = userService.updateProfile(userId, request.getDisplayName(), request.getAbout());
            
            UserDetailsResponse response = new UserDetailsResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName(),
                user.getAbout(),
                user.getProfilePictureUrl()
            );
            
            System.out.println("Profile updated successfully for user ID: " + userId);
            System.out.println("Updated values: displayName=" + user.getDisplayName() + ", about=" + user.getAbout());
            System.out.println("==== PROFILE UPDATE REQUEST END ====\n");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("\n==== ERROR UPDATING PROFILE ====");
            System.err.println("Error updating profile information: " + e.getMessage());
            System.err.println("Error class: " + e.getClass().getName());
            System.err.println("Stack trace:");
            e.printStackTrace();
            System.err.println("==== END ERROR DETAILS ====\n");
            
            // Return a more detailed error response
            return ResponseEntity.badRequest().body(
                new ErrorResponse("Failed to update profile: " + e.getMessage())
            );
        }
    }

    // Debugging endpoint to check request format
    @GetMapping("/debug")
    public ResponseEntity<?> debugEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Backend API is working");
        response.put("message", "If you can see this, your API connection is functioning");
        
        System.out.println("Debug endpoint called successfully");
        return ResponseEntity.ok(response);
    }
    
    // Debugging endpoint that checks if a user ID exists
    @GetMapping("/check-id/{userId}")
    public ResponseEntity<?> checkUserId(@PathVariable Integer userId) {
        try {
            System.out.println("Checking if user ID exists: " + userId);
            
            if (userId == null) {
                return ResponseEntity.badRequest().body(
                    new ErrorResponse("User ID is null")
                );
            }
            
            boolean exists = userService.existsById(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("exists", exists);
            
            if (exists) {
                User user = userService.getUserById(userId);
                response.put("username", user.getUsername());
                response.put("email", user.getEmail());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ErrorResponse("Error checking user ID: " + e.getMessage())
            );
        }
    }
}

// Add this helper class at the end of the file
class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
} 