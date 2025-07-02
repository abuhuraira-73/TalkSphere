package com.chatapp.chatappbackend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "friendships", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user1_id", "user2_id"}))
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public Friendship() {
    }

    public Friendship(User user1, User user2) {
        // Ensure consistent ordering of users to avoid duplicate friendships
        // This way, (userA, userB) and (userB, userA) are considered the same friendship
        if (user1.getId() <= user2.getId()) {
            this.user1 = user1;
            this.user2 = user2;
        } else {
            this.user1 = user2;
            this.user2 = user1;
        }
    }

    // Getters and Setters
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Utility method to check if a user is part of this friendship
    public boolean involves(User user) {
        return user1.getId().equals(user.getId()) || user2.getId().equals(user.getId());
    }

    // Utility method to get the other user in the friendship
    public User getOtherUser(User user) {
        if (user1.getId().equals(user.getId())) {
            return user2;
        } else if (user2.getId().equals(user.getId())) {
            return user1;
        }
        throw new IllegalArgumentException("User is not part of this friendship");
    }
} 