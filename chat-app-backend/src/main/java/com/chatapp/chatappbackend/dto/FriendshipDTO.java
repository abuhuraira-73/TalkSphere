package com.chatapp.chatappbackend.dto;

import java.time.LocalDateTime;

public class FriendshipDTO {
    
    private Integer id;
    private UserDetailsResponse friend;
    private String createdAt;

    public FriendshipDTO() {
    }

    public FriendshipDTO(Integer id, UserDetailsResponse friend, String createdAt) {
        this.id = id;
        this.friend = friend;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserDetailsResponse getFriend() {
        return friend;
    }

    public void setFriend(UserDetailsResponse friend) {
        this.friend = friend;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
} 