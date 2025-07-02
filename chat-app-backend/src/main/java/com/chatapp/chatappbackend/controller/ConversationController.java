package com.chatapp.chatappbackend.controller;

import com.chatapp.chatappbackend.dto.ConversationDTO;
import com.chatapp.chatappbackend.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    /**
     * Get all conversations for the current user
     * @param userId The ID of the user (from query parameter)
     * @return List of conversation DTOs
     */
    @GetMapping
    public ResponseEntity<List<ConversationDTO>> getConversations(@RequestParam Integer userId) {
        try {
            List<ConversationDTO> conversations = conversationService.getConversationsForUser(userId);
            return ResponseEntity.ok(conversations);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving conversations", e);
        }
    }

    /**
     * Get a specific conversation
     * @param conversationId The ID of the conversation
     * @param userId The ID of the user accessing the conversation
     * @return The conversation DTO
     */
    @GetMapping("/{conversationId}")
    public ResponseEntity<ConversationDTO> getConversation(
            @PathVariable Integer conversationId,
            @RequestParam Integer userId) {
        try {
            ConversationDTO conversation = conversationService.getConversationDTO(conversationId, userId);
            return ResponseEntity.ok(conversation);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving conversation", e);
        }
    }

    /**
     * Create or get a conversation with another user
     * @param userId The ID of the current user
     * @param otherId The ID of the other participant
     * @return The conversation DTO
     */
    @PostMapping("/with/{otherId}")
    public ResponseEntity<ConversationDTO> createOrGetConversation(
            @RequestParam Integer userId,
            @PathVariable Integer otherId) {
        try {
            if (userId.equals(otherId)) {
                throw new IllegalArgumentException("Cannot create a conversation with yourself");
            }
            
            var conversation = conversationService.getOrCreateConversation(userId, otherId);
            var conversationDTO = conversationService.getConversationDTO(conversation.getId(), userId);
            return ResponseEntity.ok(conversationDTO);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating conversation", e);
        }
    }
} 