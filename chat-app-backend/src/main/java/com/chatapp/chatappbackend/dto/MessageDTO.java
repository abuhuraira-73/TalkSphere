package com.chatapp.chatappbackend.dto;

import com.chatapp.chatappbackend.model.Message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for Message information
 */
public class MessageDTO {
    private Integer id;
    private Integer conversationId;
    private UserDTO sender;
    private String content;
    private List<MessageAttachmentDTO> attachments;
    private LocalDateTime sentAt;
    private boolean isRead;
    private boolean isDelivered;
    private LocalDateTime readAt;
    private LocalDateTime deliveredAt;
    
    // Default constructor
    public MessageDTO() {
        this.attachments = new ArrayList<>();
    }
    
    // Constructor from Message entity
    public MessageDTO(Message message) {
        this.id = message.getId();
        this.conversationId = message.getConversation().getId();
        this.sender = new UserDTO(message.getSender());
        this.content = message.getContent();
        this.sentAt = message.getSentAt();
        this.isRead = message.isRead();
        this.isDelivered = message.isDelivered();
        this.readAt = message.getReadAt();
        this.deliveredAt = message.getDeliveredAt();
        
        // Convert attachments
        if (message.getAttachments() != null && !message.getAttachments().isEmpty()) {
            this.attachments = message.getAttachments().stream()
                .map(MessageAttachmentDTO::new)
                .collect(Collectors.toList());
        } else {
            this.attachments = new ArrayList<>();
        }
    }
    
    // Getters and setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getConversationId() {
        return conversationId;
    }
    
    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }
    
    public UserDTO getSender() {
        return sender;
    }
    
    public void setSender(UserDTO sender) {
        this.sender = sender;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public List<MessageAttachmentDTO> getAttachments() {
        return attachments;
    }
    
    public void setAttachments(List<MessageAttachmentDTO> attachments) {
        this.attachments = attachments;
    }
    
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
    
    public boolean isRead() {
        return isRead;
    }
    
    public void setRead(boolean read) {
        isRead = read;
    }
    
    public boolean isDelivered() {
        return isDelivered;
    }
    
    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }
    
    public LocalDateTime getReadAt() {
        return readAt;
    }
    
    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
    
    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }
    
    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
} 