package com.chatapp.chatappbackend.dto;

import com.chatapp.chatappbackend.model.RequestStatus;

public class FriendRequestDTO {
    
    private Integer id;
    private Integer senderId;
    private String senderUsername;
    private String senderDisplayName;
    private String senderProfilePictureUrl;
    private Integer receiverId;
    private String receiverUsername;
    private String receiverDisplayName;
    private String receiverProfilePictureUrl;
    private RequestStatus status;
    private String createdAt;

    // Default constructor
    public FriendRequestDTO() {
    }

    // Constructor with essential fields
    public FriendRequestDTO(Integer id, Integer senderId, String senderUsername, 
                           Integer receiverId, String receiverUsername, RequestStatus status) {
        this.id = id;
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.receiverId = receiverId;
        this.receiverUsername = receiverUsername;
        this.status = status;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getSenderDisplayName() {
        return senderDisplayName;
    }

    public void setSenderDisplayName(String senderDisplayName) {
        this.senderDisplayName = senderDisplayName;
    }

    public String getSenderProfilePictureUrl() {
        return senderProfilePictureUrl;
    }

    public void setSenderProfilePictureUrl(String senderProfilePictureUrl) {
        this.senderProfilePictureUrl = senderProfilePictureUrl;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public String getReceiverDisplayName() {
        return receiverDisplayName;
    }

    public void setReceiverDisplayName(String receiverDisplayName) {
        this.receiverDisplayName = receiverDisplayName;
    }

    public String getReceiverProfilePictureUrl() {
        return receiverProfilePictureUrl;
    }

    public void setReceiverProfilePictureUrl(String receiverProfilePictureUrl) {
        this.receiverProfilePictureUrl = receiverProfilePictureUrl;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
} 