package com.chatapp.chatappbackend.repository;

import com.chatapp.chatappbackend.model.Conversation;
import com.chatapp.chatappbackend.model.Message;
import com.chatapp.chatappbackend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    
    /**
     * Find messages by conversation, ordered by sent time (newest first)
     */
    List<Message> findByConversationOrderBySentAtDesc(Conversation conversation);
    
    /**
     * Find messages by conversation with pagination, ordered by sent time (oldest first)
     */
    Page<Message> findByConversationAndDeletedFalseOrderBySentAt(Conversation conversation, Pageable pageable);
    
    /**
     * Find messages older than a specific message in a conversation
     */
    @Query("SELECT m FROM Message m WHERE m.conversation = :conversation AND m.sentAt < :sentAt " +
           "AND m.deleted = false ORDER BY m.sentAt DESC")
    Page<Message> findOlderMessages(@Param("conversation") Conversation conversation, 
                                   @Param("sentAt") LocalDateTime sentAt,
                                   Pageable pageable);
    
    /**
     * Find messages newer than a specific message in a conversation
     */
    @Query("SELECT m FROM Message m WHERE m.conversation = :conversation AND m.sentAt > :sentAt " +
           "AND m.deleted = false ORDER BY m.sentAt ASC")
    List<Message> findNewerMessages(@Param("conversation") Conversation conversation, 
                                    @Param("sentAt") LocalDateTime sentAt);
    
    /**
     * Find unread messages for a user in a specific conversation
     */
    @Query("SELECT m FROM Message m WHERE m.conversation = :conversation " +
           "AND m.sender != :user AND m.read = false AND m.deleted = false " +
           "ORDER BY m.sentAt ASC")
    List<Message> findUnreadMessagesInConversation(@Param("conversation") Conversation conversation, 
                                                  @Param("user") User user);
    
    /**
     * Count unread messages for a user across all conversations
     */
    @Query("SELECT COUNT(m) FROM Message m JOIN m.conversation c " +
           "WHERE (c.user1 = :user OR c.user2 = :user) " +
           "AND m.sender != :user AND m.read = false AND m.deleted = false")
    long countTotalUnreadMessagesForUser(@Param("user") User user);
    
    /**
     * Mark messages as read in a conversation for a user
     */
    @Modifying
    @Query("UPDATE Message m SET m.read = true, m.readAt = CURRENT_TIMESTAMP " +
           "WHERE m.conversation = :conversation " +
           "AND m.sender != :user AND m.read = false AND m.deleted = false")
    int markMessagesAsRead(@Param("conversation") Conversation conversation, @Param("user") User user);
    
    /**
     * Mark a message as delivered
     */
    @Modifying
    @Query("UPDATE Message m SET m.delivered = true, m.deliveredAt = CURRENT_TIMESTAMP " +
           "WHERE m.id = :messageId AND m.delivered = false")
    int markMessageAsDelivered(@Param("messageId") Integer messageId);
    
    /**
     * Soft delete a message
     */
    @Modifying
    @Query("UPDATE Message m SET m.deleted = true WHERE m.id = :messageId")
    int softDeleteMessage(@Param("messageId") Integer messageId);
    
    /**
     * Mark specific messages as read in a conversation for a user
     */
    @Modifying
    @Query("UPDATE Message m SET m.read = true, m.readAt = CURRENT_TIMESTAMP " +
           "WHERE m.conversation = :conversation " +
           "AND m.sender != :user " +
           "AND m.id IN :messageIds " +
           "AND m.read = false " +
           "AND m.deleted = false")
    int markSpecificMessagesAsRead(@Param("conversation") Conversation conversation, 
                                 @Param("user") User user,
                                 @Param("messageIds") List<Integer> messageIds);
} 