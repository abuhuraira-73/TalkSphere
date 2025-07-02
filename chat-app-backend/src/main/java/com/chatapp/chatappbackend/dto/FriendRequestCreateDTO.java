package com.chatapp.chatappbackend.dto;

public class FriendRequestCreateDTO {
    
    private Integer senderId;
    private Integer receiverId;

    public FriendRequestCreateDTO() {
    }

    public FriendRequestCreateDTO(Integer senderId, Integer receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }
} 