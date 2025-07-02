package com.chatapp.chatappbackend.service;

import com.chatapp.chatappbackend.model.User;
import com.chatapp.chatappbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public UserService(UserRepository userRepository, FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    public User getUserById(Integer userId) {
        try {
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            
            return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        } catch (Exception e) {
            System.err.println("Error in getUserById method: " + e.getMessage());
            throw e;
        }
    }
    
    public User updateProfilePicture(Integer userId, MultipartFile profilePicture) {
        User user = getUserById(userId);
        
        // Store the file and get the filename
        String filename = fileStorageService.storeProfilePicture(profilePicture);
        
        // Set the profile picture URL for the user
        user.setProfilePictureUrl("/uploads/profile-pictures/" + filename);
        
        // Save the updated user
        return userRepository.save(user);
    }
    
    public User updateProfile(Integer userId, String displayName, String about) {
        try {
            User user = getUserById(userId);
            
            // Log the current and new values
            System.out.println("Current displayName: " + user.getDisplayName());
            System.out.println("New displayName: " + displayName);
            System.out.println("Current about: " + user.getAbout());
            System.out.println("New about: " + about);
            
            // Update fields, allowing empty strings (to clear values)
            user.setDisplayName(displayName);
            user.setAbout(about);
            
            // Save the updated user
            User savedUser = userRepository.save(user);
            System.out.println("User saved with displayName: " + savedUser.getDisplayName() + ", about: " + savedUser.getAbout());
            
            return savedUser;
        } catch (Exception e) {
            System.err.println("Error in updateProfile method: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update user profile: " + e.getMessage(), e);
        }
    }

    public boolean existsById(Integer userId) {
        if (userId == null) {
            return false;
        }
        return userRepository.existsById(userId);
    }
    
    public List<User> getAllUsersExceptCurrent(Integer currentUserId) {
        try {
            // Get all users from the repository
            List<User> allUsers = userRepository.findAll();
            
            // Filter out the current user
            return allUsers.stream()
                .filter(user -> !user.getId().equals(currentUserId))
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error in getAllUsersExceptCurrent method: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch users: " + e.getMessage(), e);
        }
    }
} 