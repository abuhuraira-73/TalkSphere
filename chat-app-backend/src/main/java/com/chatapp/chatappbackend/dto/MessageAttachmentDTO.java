package com.chatapp.chatappbackend.dto;

import com.chatapp.chatappbackend.model.AttachmentType;
import com.chatapp.chatappbackend.model.MessageAttachment;

/**
 * Data Transfer Object for MessageAttachment information
 */
public class MessageAttachmentDTO {
    private Integer id;
    private Integer messageId;
    private AttachmentType attachmentType;
    private String fileName;
    private String originalFileName;
    private String mimeType;
    private Long fileSize;
    private String thumbnailUrl;
    private String downloadUrl;
    
    // Default constructor
    public MessageAttachmentDTO() {
    }
    
    // Constructor from MessageAttachment entity
    public MessageAttachmentDTO(MessageAttachment attachment) {
        this.id = attachment.getId();
        this.messageId = attachment.getMessage().getId();
        this.attachmentType = attachment.getAttachmentType();
        this.fileName = attachment.getFileName();
        this.originalFileName = attachment.getOriginalFileName();
        this.mimeType = attachment.getMimeType();
        this.fileSize = attachment.getFileSize();
        
        // Use the file path directly for download URL
        this.downloadUrl = attachment.getFilePath();
        
        // Use thumbnail path if available, otherwise use the file path
        this.thumbnailUrl = attachment.getThumbnailPath() != null ? 
            attachment.getThumbnailPath() : attachment.getFilePath();
    }
    
    // Getters and setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getMessageId() {
        return messageId;
    }
    
    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }
    
    public AttachmentType getAttachmentType() {
        return attachmentType;
    }
    
    public void setAttachmentType(AttachmentType attachmentType) {
        this.attachmentType = attachmentType;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getOriginalFileName() {
        return originalFileName;
    }
    
    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
    
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
    
    public String getDownloadUrl() {
        return downloadUrl;
    }
    
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
} 