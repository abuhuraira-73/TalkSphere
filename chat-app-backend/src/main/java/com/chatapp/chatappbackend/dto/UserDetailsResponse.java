package com.chatapp.chatappbackend.dto;

public class UserDetailsResponse {
    private Integer id;
    private String username;
    private String email;
    private String displayName;
    private String about;
    private String profilePictureUrl;

    public UserDetailsResponse() {
    }

    public UserDetailsResponse(Integer id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
    
    public UserDetailsResponse(Integer id, String username, String email, String profilePictureUrl) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
    }
    
    public UserDetailsResponse(Integer id, String username, String email, String displayName, String about, String profilePictureUrl) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.about = about;
        this.profilePictureUrl = profilePictureUrl;
    }

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