package com.digital.employee.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileService {
    
    @Value("${file.upload.path}")
    private String uploadPath;
    
    @Value("${file.upload.allowed-extensions}")
    private String allowedExtensions;
    
    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("文件名无效");
        }
        
        String extension = getFileExtension(originalFilename);
        if (!isAllowedExtension(extension)) {
            throw new IllegalArgumentException("不支持的文件类型: " + extension);
        }
        
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        String newFilename = UUID.randomUUID().toString() + "." + extension;
        Path targetPath = Paths.get(uploadPath, newFilename);
        
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        log.info("文件上传成功: {}", targetPath);
        
        return "/api/files/" + newFilename;
    }
    
    public byte[] getFile(String filename) throws IOException {
        Path filePath = Paths.get(uploadPath, filename);
        if (Files.exists(filePath)) {
            return Files.readAllBytes(filePath);
        }
        return null;
    }
    
    public boolean deleteFile(String filename) {
        try {
            Path filePath = Paths.get(uploadPath, filename);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("删除文件失败: {}", filename, e);
            return false;
        }
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }
    
    private boolean isAllowedExtension(String extension) {
        String[] allowed = allowedExtensions.split(",");
        for (String ext : allowed) {
            if (ext.trim().equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
}
