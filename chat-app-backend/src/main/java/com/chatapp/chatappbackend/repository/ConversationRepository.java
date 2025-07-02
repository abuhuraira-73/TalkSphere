package com.chatapp.chatappbackend.repository;

import com.chatapp.chatappbackend.model.Conversation;
import com.chatapp.chatappbackend.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Integer> {
    
    /**
     * Find a conversation between two users
     */
    @Query("SELECT c FROM Conversation c WHERE (c.user1 = :user1 AND c.user2 = :user2) OR (c.user1 = :user2 AND c.user2 = :user1)")
    Optional<Conversation> findConversationBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);
    
    /**
     * Find all conversations involving a user, ordered by the most recent message
     */
    @Query("SELECT c FROM Conversation c WHERE c.user1 = :user OR c.user2 = :user ORDER BY c.lastMessageTime DESC")
    List<Conversation> findConversationsForUser(@Param("user") User user);
    
    /**
     * Find all conversations involving a user with pagination
     */
    @Query("SELECT c FROM Conversation c WHERE c.user1.id = :userId OR c.user2.id = :userId ORDER BY c.lastMessageTime DESC")
    List<Conversation> findConversationsForUserWithLimit(@Param("userId") Integer userId, Pageable pageable);
    
    /**
     * Count unread messages in a conversation for a user
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation.id = :conversationId " +
           "AND m.sender.id != :userId AND m.read = false AND m.deleted = false")
    long countUnreadMessagesInConversation(@Param("conversationId") Integer conversationId, 
                                          @Param("userId") Integer userId);
} 