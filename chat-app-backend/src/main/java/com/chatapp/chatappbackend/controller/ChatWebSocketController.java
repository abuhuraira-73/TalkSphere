package com.chatapp.chatappbackend.controller;

import com.chatapp.chatappbackend.dto.MessageDTO;
import com.chatapp.chatappbackend.model.Message;
import com.chatapp.chatappbackend.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class ChatWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageService messageService;

    /**
     * Handle a chat message sent through WebSocket
     * @param senderId The ID of the sender
     * @param recipientId The ID of the recipient
     * @param payload The message content
     */
    @MessageMapping("/chat/{senderId}/{recipientId}")
    public void sendMessage(
            @DestinationVariable Integer senderId,
            @DestinationVariable Integer recipientId,
            @Payload Map<String, String> payload) {
        
        String content = payload.get("content");
        if (content == null || content.trim().isEmpty()) {
            return;
        }
        
        try {
            // Save the message to the database
            MessageDTO message = messageService.sendMessage(senderId, recipientId, content);
            
            // Send the message to the recipient's queue
            messagingTemplate.convertAndSendToUser(
                recipientId.toString(),
                "/queue/messages",
                message
            );
            
            // Also send a copy to the sender's queue for confirmation
            messagingTemplate.convertAndSendToUser(
                senderId.toString(),
                "/queue/messages",
                message
            );
        } catch (Exception e) {
            // Send error message back to sender
            messagingTemplate.convertAndSendToUser(
                senderId.toString(),
                "/queue/errors",
                Map.of("error", "Failed to send message: " + e.getMessage())
            );
        }
    }

    /**
     * Handle a chat message sent to an existing conversation
     * @param conversationId The ID of the conversation
     * @param senderId The ID of the sender
     * @param payload The message content
     */
    @MessageMapping("/conversation/{conversationId}/{senderId}")
    public void sendMessageToConversation(
            @DestinationVariable Integer conversationId,
            @DestinationVariable Integer senderId,
            @Payload Map<String, String> payload) {
        
        String content = payload.get("content");
        if (content == null || content.trim().isEmpty()) {
            return;
        }
        
        try {
            // Save the message to the database
            MessageDTO message = messageService.sendMessageToConversation(conversationId, senderId, content);
            
            // Send to the conversation topic
            messagingTemplate.convertAndSend(
                "/topic/conversation." + conversationId,
                message
            );
        } catch (Exception e) {
            // Send error message back to sender
            messagingTemplate.convertAndSendToUser(
                senderId.toString(),
                "/queue/errors",
                Map.of("error", "Failed to send message: " + e.getMessage())
            );
        }
    }

    /**
     * Handle read receipt notification
     * @param conversationId The ID of the conversation
     * @param userId The ID of the user who read the messages
     */
    @MessageMapping("/conversation/{conversationId}/read/{userId}")
    public void markMessagesAsRead(
            @DestinationVariable Integer conversationId,
            @DestinationVariable Integer userId) {
        
        try {
            // Mark messages as read in the database
            int count = messageService.markMessagesAsRead(conversationId, userId);
            
            // Notify the conversation about the read status change
            messagingTemplate.convertAndSend(
                "/topic/conversation." + conversationId + ".read",
                Map.of(
                    "userId", userId,
                    "count", count,
                    "timestamp", System.currentTimeMillis()
                )
            );
        } catch (Exception e) {
            // Send error message back to the user
            messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/errors",
                Map.of("error", "Failed to mark messages as read: " + e.getMessage())
            );
        }
    }

    /**
     * Handle typing indicator notification
     * @param conversationId The ID of the conversation
     * @param userId The ID of the user who is typing
     * @param payload The typing status payload
     */
    @MessageMapping("/conversation/{conversationId}/typing/{userId}")
    public void sendTypingNotification(
            @DestinationVariable Integer conversationId,
            @DestinationVariable Integer userId,
            @Payload Map<String, Object> payload) {
        
        Boolean isTyping = (Boolean) payload.getOrDefault("isTyping", false);
        
        // Send typing notification to the conversation
        messagingTemplate.convertAndSend(
            "/topic/conversation." + conversationId + ".typing",
            Map.of(
                "userId", userId,
                "isTyping", isTyping,
                "timestamp", System.currentTimeMillis()
            )
        );
    }
} 