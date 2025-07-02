package com.chatapp.chatappbackend.repository;

import com.chatapp.chatappbackend.model.AttachmentType;
import com.chatapp.chatappbackend.model.Message;
import com.chatapp.chatappbackend.model.MessageAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageAttachmentRepository extends JpaRepository<MessageAttachment, Integer> {
    
    /**
     * Find all attachments for a message
     */
    List<MessageAttachment> findByMessage(Message message);
    
    /**
     * Find attachments by message and type
     */
    List<MessageAttachment> findByMessageAndAttachmentType(Message message, AttachmentType attachmentType);
    
    /**
     * Find all image attachments for a conversation
     */
    @Query("SELECT a FROM MessageAttachment a " +
           "JOIN a.message m " +
           "WHERE m.conversation.id = :conversationId " +
           "AND a.attachmentType = 'IMAGE' " +
           "AND m.deleted = false " +
           "ORDER BY a.createdAt DESC")
    List<MessageAttachment> findAllImagesInConversation(@Param("conversationId") Integer conversationId);
    
    /**
     * Find all document attachments for a conversation
     */
    @Query("SELECT a FROM MessageAttachment a " +
           "JOIN a.message m " +
           "WHERE m.conversation.id = :conversationId " +
           "AND a.attachmentType = 'DOCUMENT' " +
           "AND m.deleted = false " +
           "ORDER BY a.createdAt DESC")
    List<MessageAttachment> findAllDocumentsInConversation(@Param("conversationId") Integer conversationId);
} 