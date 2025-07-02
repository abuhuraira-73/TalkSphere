package com.chatapp.chatappbackend.service;

import com.chatapp.chatappbackend.dto.ConversationDTO;
import com.chatapp.chatappbackend.model.Conversation;
import com.chatapp.chatappbackend.model.User;
import com.chatapp.chatappbackend.repository.ConversationRepository;
import com.chatapp.chatappbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Get or create a conversation between two users
     * @param user1Id The ID of the first user
     * @param user2Id The ID of the second user
     * @return The conversation entity
     */
    @Transactional
    public Conversation getOrCreateConversation(Integer user1Id, Integer user2Id) {
        User user1 = userRepository.findById(user1Id)
            .orElseThrow(() -> new IllegalArgumentException("User with ID " + user1Id + " not found"));
        
        User user2 = userRepository.findById(user2Id)
            .orElseThrow(() -> new IllegalArgumentException("User with ID " + user2Id + " not found"));
        
        // Check if conversation already exists
        Optional<Conversation> existingConversation = conversationRepository.findConversationBetweenUsers(user1, user2);
        
        if (existingConversation.isPresent()) {
            return existingConversation.get();
        }
        
        // Create new conversation if it doesn't exist
        Conversation newConversation = new Conversation(user1, user2);
        return conversationRepository.save(newConversation);
    }
    
    /**
     * Get all conversations for a user
     * @param userId The ID of the user
     * @return List of conversation DTOs
     */
    @Transactional(readOnly = true)
    public List<ConversationDTO> getConversationsForUser(Integer userId) {
        User currentUser = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));
        
        List<Conversation> conversations = conversationRepository.findConversationsForUser(currentUser);
        
        return conversations.stream()
            .map(conversation -> {
                long unreadCount = conversationRepository.countUnreadMessagesInConversation(
                    conversation.getId(), userId);
                return new ConversationDTO(conversation, currentUser, (int) unreadCount);
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Get a conversation by ID, ensuring the user is a participant
     * @param conversationId The ID of the conversation
     * @param userId The ID of the user trying to access the conversation
     * @return The conversation entity
     * @throws IllegalArgumentException if the conversation doesn't exist or user is not a participant
     */
    @Transactional(readOnly = true)
    public Conversation getConversationForUser(Integer conversationId, Integer userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new IllegalArgumentException("Conversation with ID " + conversationId + " not found"));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));
        
        if (!conversation.hasParticipant(user)) {
            throw new IllegalArgumentException("User is not a participant in this conversation");
        }
        
        return conversation;
    }
    
    /**
     * Get a conversation DTO for a specific conversation
     * @param conversationId The ID of the conversation
     * @param userId The ID of the user accessing the conversation
     * @return The conversation DTO
     */
    @Transactional(readOnly = true)
    public ConversationDTO getConversationDTO(Integer conversationId, Integer userId) {
        Conversation conversation = getConversationForUser(conversationId, userId);
        User currentUser = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));
        
        long unreadCount = conversationRepository.countUnreadMessagesInConversation(conversationId, userId);
        return new ConversationDTO(conversation, currentUser, (int) unreadCount);
    }

    /**
     * Get conversations for a user with a limit
     * @param userId The ID of the user
     * @param limit The maximum number of conversations to return
     * @return List of conversation DTOs
     */
    @Transactional(readOnly = true)
    public List<ConversationDTO> getConversationsForUserWithLimit(Integer userId, int limit) {
        User currentUser = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));
        
        // Use PageRequest instead of raw limit parameter
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "lastMessageTime"));
        List<Conversation> conversations = conversationRepository.findConversationsForUserWithLimit(userId, pageRequest);
        
        return conversations.stream()
            .map(conversation -> {
                long unreadCount = conversationRepository.countUnreadMessagesInConversation(
                    conversation.getId(), userId);
                return new ConversationDTO(conversation, currentUser, (int) unreadCount);
            })
            .collect(Collectors.toList());
    }
} 