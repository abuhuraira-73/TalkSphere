package com.chatapp.chatappbackend.service;

import com.chatapp.chatappbackend.dto.FriendRequestDTO;
import com.chatapp.chatappbackend.dto.FriendshipDTO;
import com.chatapp.chatappbackend.dto.UserDetailsResponse;
import com.chatapp.chatappbackend.model.FriendRequest;
import com.chatapp.chatappbackend.model.Friendship;
import com.chatapp.chatappbackend.model.RequestStatus;
import com.chatapp.chatappbackend.model.User;
import com.chatapp.chatappbackend.repository.FriendRequestRepository;
import com.chatapp.chatappbackend.repository.FriendshipRepository;
import com.chatapp.chatappbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendshipService {

    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public FriendshipService(UserRepository userRepository, 
                           FriendRequestRepository friendRequestRepository,
                           FriendshipRepository friendshipRepository) {
        this.userRepository = userRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.friendshipRepository = friendshipRepository;
    }

    /**
     * Send a friend request from one user to another
     */
    @Transactional
    public FriendRequestDTO sendFriendRequest(Integer senderId, Integer receiverId) {
        // Validate inputs
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Cannot send friend request to yourself");
        }

        // Get user entities
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        // Check if they are already friends
        if (friendshipRepository.areFriends(sender, receiver)) {
            throw new IllegalArgumentException("Users are already friends");
        }

        // Check if a request already exists
        Optional<FriendRequest> existingRequest = friendRequestRepository.findBySenderAndReceiver(sender, receiver);
        if (existingRequest.isPresent()) {
            FriendRequest request = existingRequest.get();
            if (request.getStatus() == RequestStatus.PENDING) {
                throw new IllegalArgumentException("Friend request already sent and pending");
            } else if (request.getStatus() == RequestStatus.ACCEPTED) {
                throw new IllegalArgumentException("Friend request already accepted");
            } else {
                // If it was rejected before, allow resending
                request.setStatus(RequestStatus.PENDING);
                return convertToDTO(friendRequestRepository.save(request));
            }
        }

        // Also check if reverse request exists (receiver -> sender)
        Optional<FriendRequest> reverseRequest = friendRequestRepository.findBySenderAndReceiver(receiver, sender);
        if (reverseRequest.isPresent()) {
            FriendRequest request = reverseRequest.get();
            if (request.getStatus() == RequestStatus.PENDING) {
                throw new IllegalArgumentException("There is already a pending request from the receiver");
            }
        }

        // Create and save the friend request
        FriendRequest friendRequest = new FriendRequest(sender, receiver);
        FriendRequest savedRequest = friendRequestRepository.save(friendRequest);
        
        return convertToDTO(savedRequest);
    }

    /**
     * Accept a friend request
     */
    @Transactional
    public FriendshipDTO acceptFriendRequest(Integer requestId, Integer userId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));

        // Verify the current user is the receiver of the request
        if (!request.getReceiver().getId().equals(userId)) {
            throw new IllegalArgumentException("Only the receiver can accept a friend request");
        }

        // Verify the request is pending
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Friend request is not pending");
        }

        // Update request status
        request.setStatus(RequestStatus.ACCEPTED);
        friendRequestRepository.save(request);

        // Create friendship entry
        Friendship friendship = new Friendship(request.getSender(), request.getReceiver());
        Friendship savedFriendship = friendshipRepository.save(friendship);

        // Return the friend data
        User friend = request.getSender(); // The friend is the sender
        UserDetailsResponse friendResponse = new UserDetailsResponse(
            friend.getId(),
            friend.getUsername(),
            friend.getEmail(),
            friend.getDisplayName(),
            friend.getAbout(),
            friend.getProfilePictureUrl()
        );

        return new FriendshipDTO(
            savedFriendship.getId(),
            friendResponse,
            savedFriendship.getCreatedAt().format(DATE_FORMATTER)
        );
    }

    /**
     * Reject a friend request
     */
    @Transactional
    public FriendRequestDTO rejectFriendRequest(Integer requestId, Integer userId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));

        // Verify the current user is the receiver of the request
        if (!request.getReceiver().getId().equals(userId)) {
            throw new IllegalArgumentException("Only the receiver can reject a friend request");
        }

        // Verify the request is pending
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Friend request is not pending");
        }

        // Update request status
        request.setStatus(RequestStatus.REJECTED);
        FriendRequest savedRequest = friendRequestRepository.save(request);

        return convertToDTO(savedRequest);
    }

    /**
     * Get all pending friend requests received by a user
     */
    public List<FriendRequestDTO> getPendingRequestsReceived(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<FriendRequest> requests = friendRequestRepository
                .findByReceiverAndStatus(user, RequestStatus.PENDING);

        return requests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all pending friend requests sent by a user
     */
    public List<FriendRequestDTO> getPendingRequestsSent(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<FriendRequest> requests = friendRequestRepository
                .findBySenderAndStatus(user, RequestStatus.PENDING);

        return requests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all friends of a user
     */
    public List<FriendshipDTO> getFriends(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Get both lists of friends and combine them
        List<User> friends1 = friendshipRepository.findUser1ByUser2Id(userId);
        List<User> friends2 = friendshipRepository.findUser2ByUser1Id(userId);
        
        // Combine both lists
        List<User> allFriends = new ArrayList<>(friends1);
        allFriends.addAll(friends2);
        
        List<FriendshipDTO> result = new ArrayList<>();

        // Find the corresponding friendships and convert to DTOs
        for (User friend : allFriends) {
            Optional<Friendship> friendshipOpt = friendshipRepository.findByUsers(user, friend);
            if (friendshipOpt.isPresent()) {
                Friendship friendship = friendshipOpt.get();
                
                UserDetailsResponse friendResponse = new UserDetailsResponse(
                    friend.getId(),
                    friend.getUsername(),
                    friend.getEmail(),
                    friend.getDisplayName(),
                    friend.getAbout(),
                    friend.getProfilePictureUrl()
                );
                
                FriendshipDTO dto = new FriendshipDTO(
                    friendship.getId(),
                    friendResponse,
                    friendship.getCreatedAt().format(DATE_FORMATTER)
                );
                
                result.add(dto);
            }
        }

        return result;
    }
    
    /**
     * Check if there's an existing friendship or pending request between users
     */
    public String checkRelationship(Integer userId1, Integer userId2) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new IllegalArgumentException("User 1 not found"));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new IllegalArgumentException("User 2 not found"));
                
        // Check if they're already friends
        if (friendshipRepository.areFriends(user1, user2)) {
            return "FRIENDS";
        }
        
        // Check for pending request from user1 to user2
        Optional<FriendRequest> request1to2 = friendRequestRepository.findBySenderAndReceiver(user1, user2);
        if (request1to2.isPresent()) {
            return request1to2.get().getStatus().toString();
        }
        
        // Check for pending request from user2 to user1
        Optional<FriendRequest> request2to1 = friendRequestRepository.findBySenderAndReceiver(user2, user1);
        if (request2to1.isPresent()) {
            return "REVERSE_" + request2to1.get().getStatus().toString();
        }
        
        // No relationship
        return "NONE";
    }

    /**
     * Helper method to convert a FriendRequest entity to DTO
     */
    private FriendRequestDTO convertToDTO(FriendRequest request) {
        FriendRequestDTO dto = new FriendRequestDTO(
            request.getId(),
            request.getSender().getId(),
            request.getSender().getUsername(),
            request.getReceiver().getId(),
            request.getReceiver().getUsername(),
            request.getStatus()
        );
        
        // Add additional details
        dto.setSenderDisplayName(request.getSender().getDisplayName());
        dto.setSenderProfilePictureUrl(request.getSender().getProfilePictureUrl());
        dto.setReceiverDisplayName(request.getReceiver().getDisplayName());
        dto.setReceiverProfilePictureUrl(request.getReceiver().getProfilePictureUrl());
        dto.setCreatedAt(request.getCreatedAt().format(DATE_FORMATTER));
        
        return dto;
    }

    /**
     * Remove friendship between two users
     */
    @Transactional
    public boolean removeFriendship(Integer userId, Integer friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("Friend not found"));
        
        // Find friendship
        Optional<Friendship> friendship = friendshipRepository.findByUsers(user, friend);
        if (!friendship.isPresent()) {
            throw new IllegalArgumentException("Friendship not found");
        }
        
        // Delete the friendship
        friendshipRepository.delete(friendship.get());
        
        // Also delete any friend requests between these users
        Optional<FriendRequest> request1 = friendRequestRepository.findBySenderAndReceiver(user, friend);
        request1.ifPresent(friendRequestRepository::delete);
        
        Optional<FriendRequest> request2 = friendRequestRepository.findBySenderAndReceiver(friend, user);
        request2.ifPresent(friendRequestRepository::delete);
        
        return true;
    }
} 