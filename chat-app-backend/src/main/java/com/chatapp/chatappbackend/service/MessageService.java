package com.chatapp.chatappbackend.service;

import com.chatapp.chatappbackend.dto.MessageDTO;
import com.chatapp.chatappbackend.model.Conversation;
import com.chatapp.chatappbackend.model.Message;
import com.chatapp.chatappbackend.model.MessageAttachment;
import com.chatapp.chatappbackend.model.User;
import com.chatapp.chatappbackend.repository.ConversationRepository;
import com.chatapp.chatappbackend.repository.MessageRepository;
import com.chatapp.chatappbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ConversationService conversationService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    /**
     * Send a new message in a conversation
     * @param senderId The ID of the sender
     * @param recipientId The ID of the recipient
     * @param content The message content
     * @return The created message DTO
     */
    @Transactional
    public MessageDTO sendMessage(Integer senderId, Integer recipientId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }
        
        // Get or create conversation between the two users
        Conversation conversation = conversationService.getOrCreateConversation(senderId, recipientId);
        
        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new IllegalArgumentException("Sender with ID " + senderId + " not found"));
        
        // Create a new message
        Message message = new Message(conversation, sender, content);
        message.setDelivered(false);
        message.setRead(false);
        
        // Save the message
        message = messageRepository.save(message);
        
        // Update the conversation's last message info
        conversation.setLastMessageText(content);
        conversation.setLastMessageTime(message.getSentAt());
        conversation.setLastMessageSenderId(senderId);
        conversationRepository.save(conversation);
        
        return new MessageDTO(message);
    }
    
    /**
     * Send a message in an existing conversation
     * @param conversationId The ID of the conversation
     * @param senderId The ID of the sender
     * @param content The message content
     * @return The created message DTO
     */
    @Transactional
    public MessageDTO sendMessageToConversation(Integer conversationId, Integer senderId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }
        
        // Verify the conversation exists and the user is a participant
        Conversation conversation = conversationService.getConversationForUser(conversationId, senderId);
        
        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new IllegalArgumentException("Sender with ID " + senderId + " not found"));
        
        // Create a new message
        Message message = new Message(conversation, sender, content);
        message.setDelivered(false);
        message.setRead(false);
        
        // Save the message
        message = messageRepository.save(message);
        
        // Update the conversation's last message info
        conversation.setLastMessageText(content);
        conversation.setLastMessageTime(message.getSentAt());
        conversation.setLastMessageSenderId(senderId);
        conversationRepository.save(conversation);
        
        return new MessageDTO(message);
    }
    
    /**
     * Get messages for a conversation with pagination
     * @param conversationId The ID of the conversation
     * @param userId The ID of the user requesting the messages
     * @param page The page number (0-based)
     * @param size The page size
     * @return List of message DTOs, oldest first
     */
    @Transactional(readOnly = true)
    public List<MessageDTO> getMessagesForConversation(Integer conversationId, Integer userId, int page, int size) {
        // Verify the conversation exists and the user is a participant
        Conversation conversation = conversationService.getConversationForUser(conversationId, userId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"));
        Page<Message> messagePage = messageRepository.findByConversationAndDeletedFalseOrderBySentAt(conversation, pageable);
        
        return messagePage.getContent().stream()
            .map(MessageDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get messages older than a specific message in a conversation
     * @param conversationId The ID of the conversation
     * @param userId The ID of the user requesting the messages
     * @param messageId The ID of the reference message
     * @param limit The maximum number of messages to return
     * @return List of message DTOs, newest first (closer to the reference message first)
     */
    @Transactional(readOnly = true)
    public List<MessageDTO> getOlderMessages(Integer conversationId, Integer userId, Integer messageId, int limit) {
        // Verify the conversation exists and the user is a participant
        Conversation conversation = conversationService.getConversationForUser(conversationId, userId);
        
        Message referenceMessage = messageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("Message with ID " + messageId + " not found"));
        
        if (!referenceMessage.getConversation().getId().equals(conversationId)) {
            throw new IllegalArgumentException("Referenced message is not part of the specified conversation");
        }
        
        Pageable pageable = PageRequest.of(0, limit);
        Page<Message> messagePage = messageRepository.findOlderMessages(
            conversation, referenceMessage.getSentAt(), pageable);
        
        return messagePage.getContent().stream()
            .map(MessageDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get newer messages than a specific message in a conversation
     * @param conversationId The ID of the conversation
     * @param userId The ID of the user requesting the messages
     * @param messageId The ID of the reference message
     * @return List of message DTOs, oldest first (starting from just after the reference message)
     */
    @Transactional(readOnly = true)
    public List<MessageDTO> getNewerMessages(Integer conversationId, Integer userId, Integer messageId) {
        // Verify the conversation exists and the user is a participant
        Conversation conversation = conversationService.getConversationForUser(conversationId, userId);
        
        Message referenceMessage = messageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("Message with ID " + messageId + " not found"));
        
        if (!referenceMessage.getConversation().getId().equals(conversationId)) {
            throw new IllegalArgumentException("Referenced message is not part of the specified conversation");
        }
        
        List<Message> messages = messageRepository.findNewerMessages(conversation, referenceMessage.getSentAt());
        
        return messages.stream()
            .map(MessageDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Mark specific messages in a conversation as read
     * @param conversationId The ID of the conversation
     * @param userId The ID of the user who read the messages
     * @param messageIds The list of message IDs to mark as read
     * @return The number of messages marked as read
     */
    @Transactional
    public int markMessagesAsRead(Integer conversationId, Integer userId, List<Integer> messageIds) {
        // Verify the conversation exists and the user is a participant
        Conversation conversation = conversationService.getConversationForUser(conversationId, userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));
        
        // Mark only the specified messages as read
        return messageRepository.markSpecificMessagesAsRead(conversation, user, messageIds);
    }
    
    /**
     * Mark a specific message as delivered
     * @param messageId The ID of the message
     * @param userId The ID of the user who received the message
     * @return true if the message was marked as delivered, false otherwise
     */
    @Transactional
    public boolean markMessageAsDelivered(Integer messageId, Integer userId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("Message with ID " + messageId + " not found"));
        
        // Verify the user is a participant in the conversation
        Conversation conversation = message.getConversation();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));
        
        if (!conversation.hasParticipant(user)) {
            throw new IllegalArgumentException("User is not a participant in this conversation");
        }
        
        // Only mark as delivered if the user is the recipient (not the sender)
        if (message.getSender().getId().equals(userId)) {
            return false; // Sender can't mark their own message as delivered
        }
        
        return messageRepository.markMessageAsDelivered(messageId) > 0;
    }
    
    /**
     * Delete a message (soft delete)
     * @param messageId The ID of the message
     * @param userId The ID of the user deleting the message
     * @return true if the message was deleted, false otherwise
     */
    @Transactional
    public boolean deleteMessage(Integer messageId, Integer userId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("Message with ID " + messageId + " not found"));
        
        // Only the sender can delete their message
        if (!message.getSender().getId().equals(userId)) {
            throw new IllegalArgumentException("Only the sender can delete their message");
        }
        
        return messageRepository.softDeleteMessage(messageId) > 0;
    }
    
    /**
     * Send a message with attachments to a conversation
     * @param conversationId The ID of the conversation
     * @param senderId The ID of the sender
     * @param content The message content (optional)
     * @param attachments The list of attachments (optional)
     * @return The created message DTO
     */
    @Transactional
    public MessageDTO sendMessageWithAttachments(Integer conversationId, Integer senderId, String content, List<MultipartFile> attachments) {
        // Verify the conversation exists and the user is a participant
        Conversation conversation = conversationService.getConversationForUser(conversationId, senderId);
        
        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new IllegalArgumentException("Sender with ID " + senderId + " not found"));
        
        // Create a new message
        Message message = new Message(conversation, sender, content != null ? content : "");
        message.setDelivered(false);
        message.setRead(false);
        
        // Handle attachments if present
        if (attachments != null && !attachments.isEmpty()) {
            try {
                for (MultipartFile file : attachments) {
                    if (!file.isEmpty()) {
                        // Store the file and get the filename
                        String filename = fileStorageService.storeFile(file);
                        
                        // Determine attachment type based on content type
                        AttachmentType attachmentType = AttachmentType.OTHER;
                        String contentType = file.getContentType();
                        if (contentType != null) {
                            if (contentType.startsWith("image/")) {
                                attachmentType = AttachmentType.IMAGE;
                            } else if (contentType.startsWith("video/")) {
                                attachmentType = AttachmentType.VIDEO;
                            } else if (contentType.startsWith("audio/")) {
                                attachmentType = AttachmentType.AUDIO;
                            } else if (contentType.startsWith("application/pdf") || 
                                     contentType.startsWith("application/msword") ||
                                     contentType.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                                attachmentType = AttachmentType.DOCUMENT;
                            }
                        }
                        
                        // Create and add the attachment to the message
                        MessageAttachment attachment = new MessageAttachment();
                        attachment.setFileName(filename);
                        attachment.setOriginalFileName(file.getOriginalFilename());
                        attachment.setFilePath("/uploads/attachments/" + filename);
                        attachment.setMimeType(contentType);
                        attachment.setFileSize(file.getSize());
                        attachment.setAttachmentType(attachmentType);
                        message.addAttachment(attachment);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to store attachment", e);
            }
        }
        
        // Save the message
        message = messageRepository.save(message);
        
        // Update the conversation's last message info
        String lastMessageText = content != null && !content.isEmpty() ? content : 
            (attachments != null && !attachments.isEmpty() ? "Sent an attachment" : "");
        conversation.setLastMessageText(lastMessageText);
        conversation.setLastMessageTime(message.getSentAt());
        conversation.setLastMessageSenderId(senderId);
        conversationRepository.save(conversation);
        
        return new MessageDTO(message);
    }
} 