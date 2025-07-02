package com.chatapp.chatappbackend.repository;

import com.chatapp.chatappbackend.model.Friendship;
import com.chatapp.chatappbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {
    
    // Find friendship between two specific users
    @Query("SELECT f FROM Friendship f WHERE (f.user1 = :user1 AND f.user2 = :user2) OR (f.user1 = :user2 AND f.user2 = :user1)")
    Optional<Friendship> findByUsers(@Param("user1") User user1, @Param("user2") User user2);
    
    // Check if two users are friends
    default boolean areFriends(User user1, User user2) {
        return findByUsers(user1, user2).isPresent();
    }
    
    // Find all friends of a user
    @Query("SELECT f FROM Friendship f WHERE f.user1 = :user OR f.user2 = :user")
    List<Friendship> findByUser(@Param("user") User user);
    
    // Find all friendships involving a user
    @Query("SELECT f FROM Friendship f WHERE f.user1.id = :userId OR f.user2.id = :userId")
    List<Friendship> findAllByUserId(@Param("userId") Integer userId);
    
    // Get users who are user1 in friendships where current user is user2
    @Query("SELECT f.user1 FROM Friendship f WHERE f.user2.id = :userId")
    List<User> findUser1ByUser2Id(@Param("userId") Integer userId);
    
    // Get users who are user2 in friendships where current user is user1
    @Query("SELECT f.user2 FROM Friendship f WHERE f.user1.id = :userId")
    List<User> findUser2ByUser1Id(@Param("userId") Integer userId);
} 