package com.chatapp.chatappbackend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The two participants of the conversation
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    // Last message in this conversation for preview purposes
    @Column(name = "last_message_text", length = 500)
    private String lastMessageText;
    
    @Column(name = "last_message_time")
    private LocalDateTime lastMessageTime;
    
    @Column(name = "last_message_sender_id")
    private Integer lastMessageSenderId;

    // One-to-many relationship with Message entities
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Default constructor
    public Conversation() {
    }

    // Constructor with users
    public Conversation(User user1, User user2) {
        // Ensure consistent ordering of users
        if (user1.getId() < user2.getId()) {
            this.user1 = user1;
            this.user2 = user2;
        } else {
            this.user1 = user2;
            this.user2 = user1;
        }
    }

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public String getLastMessageText() {
        return lastMessageText;
    }

    public void setLastMessageText(String lastMessageText) {
        this.lastMessageText = lastMessageText;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
    
    public Integer getLastMessageSenderId() {
        return lastMessageSenderId;
    }
    
    public void setLastMessageSenderId(Integer lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Helper method to add a message to this conversation
    public void addMessage(Message message) {
        messages.add(message);
        message.setConversation(this);
        
        // Update the last message info
        this.lastMessageText = message.getContent();
        this.lastMessageTime = message.getSentAt();
        this.lastMessageSenderId = message.getSender().getId();
    }
    
    // Helper method to remove a message from this conversation
    public void removeMessage(Message message) {
        messages.remove(message);
        message.setConversation(null);
    }
    
    // Utility method to check if a user is part of this conversation
    public boolean hasParticipant(User user) {
        return user1.getId().equals(user.getId()) || user2.getId().equals(user.getId());
    }
    
    // Utility method to get the other participant
    public User getOtherParticipant(User user) {
        if (user1.getId().equals(user.getId())) {
            return user2;
        } else if (user2.getId().equals(user.getId())) {
            return user1;
        }
        throw new IllegalArgumentException("User is not part of this conversation");
    }
} 