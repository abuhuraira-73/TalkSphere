package com.chatapp.chatappbackend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_attachments")
public class MessageAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The message this attachment belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    // Type of attachment
    @Enumerated(EnumType.STRING)
    @Column(name = "attachment_type", nullable = false)
    private AttachmentType attachmentType;

    // The file name
    @Column(name = "file_name", nullable = false)
    private String fileName;

    // Original file name
    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    // File path on server
    @Column(name = "file_path", nullable = false)
    private String filePath;

    // MIME type
    @Column(name = "mime_type")
    private String mimeType;

    // File size in bytes
    @Column(name = "file_size")
    private Long fileSize;
    
    // Optional thumbnail for images/videos
    @Column(name = "thumbnail_path")
    private String thumbnailPath;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Default constructor
    public MessageAttachment() {
    }

    // Constructor with basic fields
    public MessageAttachment(Message message, AttachmentType attachmentType, String fileName, String originalFileName, String filePath) {
        this.message = message;
        this.attachmentType = attachmentType;
        this.fileName = fileName;
        this.originalFileName = originalFileName;
        this.filePath = filePath;
    }

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
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

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
} 