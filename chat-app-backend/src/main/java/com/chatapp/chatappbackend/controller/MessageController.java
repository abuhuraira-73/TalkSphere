package com.chatapp.chatappbackend.controller;

import com.chatapp.chatappbackend.dto.MessageDTO;
import com.chatapp.chatappbackend.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * Get messages for a conversation with pagination
     * @param conversationId The ID of the conversation
     * @param userId The ID of the user requesting the messages
     * @param page The page number (0-based)
     * @param size The page size
     * @return List of message DTOs
     */
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<MessageDTO>> getMessagesForConversation(
            @PathVariable Integer conversationId,
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            List<MessageDTO> messages = messageService.getMessagesForConversation(conversationId, userId, page, size);
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving messages", e);
        }
    }

    /**
     * Get messages older than a specific message
     * @param conversationId The ID of the conversation
     * @param userId The ID of the user requesting the messages
     * @param messageId The ID of the reference message
     * @param limit The maximum number of messages to return
     * @return List of message DTOs
     */
    @GetMapping("/conversation/{conversationId}/before/{messageId}")
    public ResponseEntity<List<MessageDTO>> getOlderMessages(
            @PathVariable Integer conversationId,
            @RequestParam Integer userId,
            @PathVariable Integer messageId,
            @RequestParam(defaultValue = "50") int limit) {
        try {
            List<MessageDTO> messages = messageService.getOlderMessages(conversationId, userId, messageId, limit);
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving older messages", e);
        }
    }

    /**
     * Get messages newer than a specific message
     * @param conversationId The ID of the conversation
     * @param userId The ID of the user requesting the messages
     * @param messageId The ID of the reference message
     * @return List of message DTOs
     */
    @GetMapping("/conversation/{conversationId}/after/{messageId}")
    public ResponseEntity<List<MessageDTO>> getNewerMessages(
            @PathVariable Integer conversationId,
            @RequestParam Integer userId,
            @PathVariable Integer messageId) {
        try {
            List<MessageDTO> messages = messageService.getNewerMessages(conversationId, userId, messageId);
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving newer messages", e);
        }
    }

    /**
     * Send a message to a user
     * @param senderId The ID of the sender
     * @param recipientId The ID of the recipient
     * @param content The message content
     * @return The created message DTO
     */
    @PostMapping("/send")
    public ResponseEntity<MessageDTO> sendMessage(
            @RequestParam Integer senderId,
            @RequestParam Integer recipientId,
            @RequestBody Map<String, String> payload) {
        try {
            String content = payload.get("content");
            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("Message content cannot be empty");
            }
            
            MessageDTO message = messageService.sendMessage(senderId, recipientId, content);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error sending message", e);
        }
    }

    /**
     * Send a message to a conversation
     * @param conversationId The ID of the conversation
     * @param senderId The ID of the sender
     * @param content The message content
     * @return The created message DTO
     */
    @PostMapping("/conversation/{conversationId}")
    public ResponseEntity<MessageDTO> sendMessageToConversation(
            @PathVariable Integer conversationId,
            @RequestParam Integer senderId,
            @RequestBody Map<String, String> payload) {
        try {
            String content = payload.get("content");
            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("Message content cannot be empty");
            }
            
            MessageDTO message = messageService.sendMessageToConversation(conversationId, senderId, content);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error sending message", e);
        }
    }

    /**
     * Mark messages in a conversation as read
     * @param conversationId The ID of the conversation
     * @param userId The ID of the user who read the messages
     * @param payload The request payload containing message IDs
     * @return The number of messages marked as read
     */
    @PutMapping("/conversation/{conversationId}/read")
    public ResponseEntity<Map<String, Integer>> markMessagesAsRead(
            @PathVariable Integer conversationId,
            @RequestParam Integer userId,
            @RequestBody Map<String, Object> payload) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> messageIds = (List<Integer>) payload.get("messageIds");
            int count = messageService.markMessagesAsRead(conversationId, userId, messageIds);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error marking messages as read", e);
        }
    }

    /**
     * Mark a message as delivered
     * @param messageId The ID of the message
     * @param userId The ID of the user who received the message
     * @return Success status
     */
    @PutMapping("/{messageId}/delivered")
    public ResponseEntity<Map<String, Boolean>> markMessageAsDelivered(
            @PathVariable Integer messageId,
            @RequestParam Integer userId) {
        try {
            boolean success = messageService.markMessageAsDelivered(messageId, userId);
            return ResponseEntity.ok(Map.of("success", success));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error marking message as delivered", e);
        }
    }

    /**
     * Delete a message
     * @param messageId The ID of the message
     * @param userId The ID of the user deleting the message
     * @return Success status
     */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Map<String, Boolean>> deleteMessage(
            @PathVariable Integer messageId,
            @RequestParam Integer userId) {
        try {
            boolean success = messageService.deleteMessage(messageId, userId);
            return ResponseEntity.ok(Map.of("success", success));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting message", e);
        }
    }

    /**
     * Send a message with attachments to a conversation
     * @param conversationId The ID of the conversation
     * @param senderId The ID of the sender
     * @param content The message content
     * @param attachments The message attachments
     * @return The created message DTO
     */
    @PostMapping(value = "/conversation/{conversationId}/with-attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDTO> sendMessageWithAttachments(
            @PathVariable Integer conversationId,
            @RequestParam Integer senderId,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) List<MultipartFile> attachments) {
        try {
            MessageDTO message = messageService.sendMessageWithAttachments(conversationId, senderId, content, attachments);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error sending message with attachments", e);
        }
    }
} 