package com.chatapp.chatappbackend.dto;

public class AuthResponse {
    private String message;
    private boolean success;
    private Integer userId;
    private Integer id;
    private String username;
    private String email;
    private String displayName;
    private String about;
    private String profilePictureUrl;

    public AuthResponse() {
    }

    public AuthResponse(String message, boolean success, Integer userId, String username) {
        this.message = message;
        this.success = success;
        this.userId = userId;
        this.id = userId;
        this.username = username;
    }
    
    public AuthResponse(String message, boolean success, Integer userId, String username, String email, String profilePictureUrl) {
        this.message = message;
        this.success = success;
        this.userId = userId;
        this.id = userId;
        this.username = username;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
    }
    
    public AuthResponse(String message, boolean success, Integer userId, String username, String email, String displayName, String about, String profilePictureUrl) {
        this.message = message;
        this.success = success;
        this.userId = userId;
        this.id = userId;
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.about = about;
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
        this.id = userId;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        this.userId = id;
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