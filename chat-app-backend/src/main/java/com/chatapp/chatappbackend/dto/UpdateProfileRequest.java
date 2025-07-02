package com.chatapp.chatappbackend.dto;

public class UpdateProfileRequest {
    private String displayName;
    private String about;

    public UpdateProfileRequest() {
    }

    public UpdateProfileRequest(String displayName, String about) {
        this.displayName = displayName;
        this.about = about;
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
    
    @Override
    public String toString() {
        return "UpdateProfileRequest{" +
                "displayName='" + displayName + '\'' +
                ", about='" + about + '\'' +
                '}';
    }
} 