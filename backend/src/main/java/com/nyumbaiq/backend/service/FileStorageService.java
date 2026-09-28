package com.nyumbaiq.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {
    @Value("${app.file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file, String subDirectory, String originalFilename) {
        String uploadPath = uploadDir + "/" + subDirectory;
        Path path = Paths.get(uploadPath);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create upload directory", e);
        }

        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String storedFilename = UUID.randomUUID() + extension;
        Path destination = path.resolve(storedFilename);

        try {
            Files.copy(file.getInputStream(), destination);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }

        return destination.toString();
    }

    public byte[] loadFile(String filePath) {
        try {
            return Files.readAllBytes(Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file", e);
        }
    }

    public void deleteFile(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            // Log but continue
        }
    }
}
