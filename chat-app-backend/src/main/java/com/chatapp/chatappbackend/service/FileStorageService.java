package com.chatapp.chatappbackend.service;

import com.chatapp.chatappbackend.dto.MessageAttachmentDTO;
import com.chatapp.chatappbackend.model.AttachmentType;
import com.chatapp.chatappbackend.model.Message;
import com.chatapp.chatappbackend.model.MessageAttachment;
import com.chatapp.chatappbackend.repository.MessageAttachmentRepository;
import com.chatapp.chatappbackend.repository.MessageRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService implements InitializingBean {

    @Value("${file.attachment-upload-dir}")
    private String attachmentUploadDir;
    
    @Value("${file.upload-dir}")
    private String profilePictureDir;
    
    @Value("${file.max-size}")
    private long maxFileSize;
    
    private Path attachmentLocation;
    private Path profilePictureLocation;
    
    // Define allowed image extensions
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = new HashSet<>(
        Arrays.asList("jpg", "jpeg", "png", "gif")
    );
    
    @Override
    public void afterPropertiesSet() throws Exception {
        this.attachmentLocation = Paths.get(attachmentUploadDir).toAbsolutePath().normalize();
        this.profilePictureLocation = Paths.get(profilePictureDir).toAbsolutePath().normalize();
        
        try {
            Files.createDirectories(this.attachmentLocation);
            Files.createDirectories(this.profilePictureLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create the directories for file storage", ex);
        }
    }
    
    public String storeProfilePicture(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty profile picture");
        }
        
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFilename.contains("..")) {
            throw new IllegalArgumentException("Filename contains invalid path sequence: " + originalFilename);
        }
        
        String fileExtension = getFileExtension(originalFilename);
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(fileExtension.toLowerCase())) {
            throw new IllegalArgumentException("Only image files are allowed for profile pictures");
        }
        
        String uniqueFilename = UUID.randomUUID().toString() + "." + fileExtension;
        
        try {
            Path targetLocation = this.profilePictureLocation.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return uniqueFilename;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to store profile picture: " + originalFilename, ex);
        }
    }
    
    public String storeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file");
        }
        
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFilename.contains("..")) {
            throw new IllegalArgumentException("Filename contains invalid path sequence: " + originalFilename);
        }
        
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + "." + fileExtension;
        
        if (file.getSize() > maxFileSize) {
            throw new IOException("File size exceeds maximum limit");
        }
        
        try {
            Path targetLocation = this.attachmentLocation.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return uniqueFilename;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to store file: " + originalFilename, ex);
        }
    }
    
    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1).toLowerCase();
    }
    
    public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = this.attachmentLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found: " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found: " + filename, ex);
        }
    }
    
    public Resource loadProfilePictureAsResource(String filename) {
        try {
            Path filePath = this.profilePictureLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("Profile picture not found: " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Profile picture not found: " + filename, ex);
        }
    }
    
    public void deleteFile(String filename) throws IOException {
        Path filePath = this.attachmentLocation.resolve(filename).normalize();
        Files.deleteIfExists(filePath);
    }

    public void deleteProfilePicture(String filename) throws IOException {
        Path filePath = this.profilePictureLocation.resolve(filename).normalize();
        Files.deleteIfExists(filePath);
    }

    public String getMimeType(String filePath) throws IOException {
        return Files.probeContentType(Paths.get(filePath));
    }

    public long getFileSize(String filePath) throws IOException {
        return Files.size(Paths.get(filePath));
    }
} 