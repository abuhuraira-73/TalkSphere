package com.chatapp.chatappbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for serving profile pictures
 */
@RestController
@RequestMapping("/api/profile-pictures")
@CrossOrigin(origins = "*") // Allow all origins for testing
public class ProfilePictureController {

    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * Debug endpoint to check profile pictures
     */
    @GetMapping("/debug")
    public ResponseEntity<Map<String, Object>> debugProfilePictures() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if the classpath directory exists
            Resource classpathDir = resourceLoader.getResource("classpath:static/uploads/profile-pictures");
            response.put("classpathDirExists", classpathDir.exists());
            
            if (classpathDir.exists()) {
                try {
                    // Try to list the contents of the classpath directory
                    File classpathDirFile = classpathDir.getFile();
                    String[] classpathFiles = classpathDirFile.list();
                    response.put("classpathFiles", classpathFiles != null ? classpathFiles : new String[0]);
                } catch (Exception e) {
                    response.put("classpathError", e.getMessage());
                }
            }
            
            // Check if the external directory exists
            File externalDir = new File("./uploads/profile-pictures");
            response.put("externalDirExists", externalDir.exists());
            response.put("externalDirPath", externalDir.getAbsolutePath());
            
            if (externalDir.exists()) {
                String[] externalFiles = externalDir.list();
                response.put("externalFiles", externalFiles != null ? externalFiles : new String[0]);
            }
            
            // Add information about where we're looking
            response.put("classpathLocation", "classpath:static/uploads/profile-pictures");
            response.put("externalLocation", "./uploads/profile-pictures");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Get a profile picture by filename
     */
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getProfilePicture(@PathVariable String filename) {
        try {
            System.out.println("Attempting to load profile picture: " + filename);
            
            // First try to load from classpath
            Resource resource = resourceLoader.getResource("classpath:static/uploads/profile-pictures/" + filename);
            
            // If resource doesn't exist in classpath, try external directory
            if (!resource.exists()) {
                System.out.println("Profile picture not found in classpath, checking external directory");
                resource = resourceLoader.getResource("file:./uploads/profile-pictures/" + filename);
            }
            
            if (!resource.exists()) {
                System.out.println("Profile picture not found in external directory");
                return ResponseEntity.notFound().build();
            }
            
            System.out.println("Profile picture found, returning resource");
            
            // Determine the content type
            String contentType = determineContentType(filename);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (Exception e) {
            System.err.println("Error serving profile picture: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Determine the content type based on file extension
     */
    private String determineContentType(String filename) {
        if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.toLowerCase().endsWith(".png")) {
            return "image/png";
        } else if (filename.toLowerCase().endsWith(".gif")) {
            return "image/gif";
        } else if (filename.toLowerCase().endsWith(".svg")) {
            return "image/svg+xml";
        }
        return "application/octet-stream";
    }
} 