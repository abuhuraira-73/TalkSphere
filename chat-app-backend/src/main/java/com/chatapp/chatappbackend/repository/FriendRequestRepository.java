package com.chatapp.chatappbackend.repository;

import com.chatapp.chatappbackend.model.FriendRequest;
import com.chatapp.chatappbackend.model.RequestStatus;
import com.chatapp.chatappbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {
    
    // Find requests sent by a user
    List<FriendRequest> findBySender(User sender);
    
    // Find requests where a user is the receiver
    List<FriendRequest> findByReceiver(User receiver);
    
    // Find pending requests received by a user
    List<FriendRequest> findByReceiverAndStatus(User receiver, RequestStatus status);
    
    // Find pending requests sent by a user
    List<FriendRequest> findBySenderAndStatus(User sender, RequestStatus status);
    
    // Find a specific request between two users
    Optional<FriendRequest> findBySenderAndReceiver(User sender, User receiver);
    
    // Check if a request already exists between two users, regardless of direction
    default boolean existsBetweenUsers(User user1, User user2) {
        return findBySenderAndReceiver(user1, user2).isPresent() 
            || findBySenderAndReceiver(user2, user1).isPresent();
    }
} 