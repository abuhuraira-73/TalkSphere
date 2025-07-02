package com.chatapp.chatappbackend.controller;

import com.chatapp.chatappbackend.model.AttachmentType;
import com.chatapp.chatappbackend.model.MessageAttachment;
import com.chatapp.chatappbackend.service.FileStorageService;
import com.chatapp.chatappbackend.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private MessageService messageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String filePath = fileStorageService.storeFile(file);
            return ResponseEntity.ok(Map.of(
                "id", UUID.randomUUID().toString(),
                "filePath", filePath,
                "originalFileName", file.getOriginalFilename()
            ));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to upload file: " + e.getMessage());
        }
    }

    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<?> downloadFile(@PathVariable String filename) {
        try {
            Resource resource = fileStorageService.loadFileAsResource(filename);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to download file: " + e.getMessage());
        }
    }

    @DeleteMapping("/{filename:.+}")
    public ResponseEntity<?> deleteFile(@PathVariable String filename) {
        try {
            fileStorageService.deleteFile(filename);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to delete file: " + e.getMessage());
        }
    }
} 