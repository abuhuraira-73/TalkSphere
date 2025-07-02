package com.chatapp.chatappbackend.dto;

import com.chatapp.chatappbackend.model.Conversation;
import com.chatapp.chatappbackend.model.User;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Conversation information
 */
public class ConversationDTO {
    private Integer id;
    private UserDTO participant; // The other user in the conversation
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private boolean isLastMessageFromMe;
    private int unreadCount;
    
    // Default constructor
    public ConversationDTO() {
    }
    
    // Constructor from Conversation entity and current user
    public ConversationDTO(Conversation conversation, User currentUser, int unreadCount) {
        this.id = conversation.getId();
        
        // Get the other participant (not the current user)
        User otherUser = conversation.getOtherParticipant(currentUser);
        this.participant = new UserDTO(otherUser);
        
        this.lastMessage = conversation.getLastMessageText();
        this.lastMessageTime = conversation.getLastMessageTime();
        
        // Check if the last message was sent by the current user
        this.isLastMessageFromMe = conversation.getLastMessageSenderId() != null && 
                                   conversation.getLastMessageSenderId().equals(currentUser.getId());
        
        this.unreadCount = unreadCount;
    }
    
    // Getters and setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public UserDTO getParticipant() {
        return participant;
    }
    
    public void setParticipant(UserDTO participant) {
        this.participant = participant;
    }
    
    public String getLastMessage() {
        return lastMessage;
    }
    
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
    
    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }
    
    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
    
    public boolean isLastMessageFromMe() {
        return isLastMessageFromMe;
    }
    
    public void setLastMessageFromMe(boolean lastMessageFromMe) {
        isLastMessageFromMe = lastMessageFromMe;
    }
    
    public int getUnreadCount() {
        return unreadCount;
    }
    
    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
} 