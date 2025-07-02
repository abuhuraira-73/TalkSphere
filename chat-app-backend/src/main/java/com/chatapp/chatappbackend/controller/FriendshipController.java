package com.chatapp.chatappbackend.controller;

import com.chatapp.chatappbackend.dto.FriendRequestCreateDTO;
import com.chatapp.chatappbackend.dto.FriendRequestDTO;
import com.chatapp.chatappbackend.dto.FriendshipDTO;
import com.chatapp.chatappbackend.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friendships")
@CrossOrigin(origins = "*")
public class FriendshipController {

    private final FriendshipService friendshipService;

    @Autowired
    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    /**
     * Send a friend request
     */
    @PostMapping("/requests")
    public ResponseEntity<?> sendFriendRequest(@RequestBody FriendRequestCreateDTO request) {
        try {
            System.out.println("Sending friend request from user " + request.getSenderId() + 
                               " to user " + request.getReceiverId());
            
            FriendRequestDTO result = friendshipService.sendFriendRequest(
                request.getSenderId(), request.getReceiverId());
            
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Accept a friend request
     */
    @PutMapping("/requests/{requestId}/accept")
    public ResponseEntity<?> acceptFriendRequest(
            @PathVariable Integer requestId,
            @RequestParam Integer userId) {
        try {
            System.out.println("Accepting friend request " + requestId + " by user " + userId);
            
            FriendshipDTO result = friendshipService.acceptFriendRequest(requestId, userId);
            
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Reject a friend request
     */
    @PutMapping("/requests/{requestId}/reject")
    public ResponseEntity<?> rejectFriendRequest(
            @PathVariable Integer requestId,
            @RequestParam Integer userId) {
        try {
            System.out.println("Rejecting friend request " + requestId + " by user " + userId);
            
            FriendRequestDTO result = friendshipService.rejectFriendRequest(requestId, userId);
            
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get received friend requests
     */
    @GetMapping("/requests/received")
    public ResponseEntity<?> getReceivedRequests(@RequestParam Integer userId) {
        try {
            System.out.println("Getting received friend requests for user " + userId);
            
            List<FriendRequestDTO> requests = friendshipService.getPendingRequestsReceived(userId);
            
            return ResponseEntity.ok(requests);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get sent friend requests
     */
    @GetMapping("/requests/sent")
    public ResponseEntity<?> getSentRequests(@RequestParam Integer userId) {
        try {
            System.out.println("Getting sent friend requests for user " + userId);
            
            List<FriendRequestDTO> requests = friendshipService.getPendingRequestsSent(userId);
            
            return ResponseEntity.ok(requests);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get user's friends
     */
    @GetMapping
    public ResponseEntity<?> getFriends(@RequestParam Integer userId) {
        try {
            System.out.println("Getting friends for user " + userId);
            
            List<FriendshipDTO> friends = friendshipService.getFriends(userId);
            
            return ResponseEntity.ok(friends);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Check relationship between two users
     */
    @GetMapping("/check")
    public ResponseEntity<?> checkRelationship(
            @RequestParam Integer userId1,
            @RequestParam Integer userId2) {
        try {
            System.out.println("Checking relationship between users " + userId1 + " and " + userId2);
            
            String relationship = friendshipService.checkRelationship(userId1, userId2);
            
            Map<String, String> result = new HashMap<>();
            result.put("relationship", relationship);
            
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Remove friendship
     */
    @DeleteMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<?> removeFriendship(
            @PathVariable Integer userId,
            @PathVariable Integer friendId) {
        try {
            System.out.println("Removing friendship between user " + userId + " and friend " + friendId);
            
            boolean result = friendshipService.removeFriendship(userId, friendId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", result);
            response.put("message", "Friendship successfully removed");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
} 