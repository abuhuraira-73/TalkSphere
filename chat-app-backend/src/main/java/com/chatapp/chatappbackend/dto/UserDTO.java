package com.chatapp.chatappbackend.dto;

import com.chatapp.chatappbackend.model.User;

/**
 * Data Transfer Object for User information.
 * This provides a limited view of user data for API responses.
 */
public class UserDTO {
    private Integer id;
    private String username;
    private String email;
    private String displayName;
    private String about;
    private String profilePictureUrl;
    
    // Default constructor
    public UserDTO() {
    }
    
    // Constructor from User entity
    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.displayName = user.getDisplayName();
        this.about = user.getAbout();
        this.profilePictureUrl = user.getProfilePictureUrl();
    }
    
    // Getters and setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getAbout() {
        return about;
    }
    
    public void setAbout(String about) {
        this.about = about;
    }
    
    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
    
    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
} 