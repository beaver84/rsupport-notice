package com.example.rsupportnotice.service;

import com.example.rsupportnotice.domain.entity.Attachment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final String uploadDir = "uploads";

    public Attachment storeFile(MultipartFile file) {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 저장 파일명 생성 (UUID + 원본명)
        String originalFileName = file.getOriginalFilename();
        String storedFileName = UUID.randomUUID() + "_" + originalFileName;

        // 저장 경로 생성
        Path filePath = Paths.get(uploadDir, storedFileName);

        try {
            // 파일 저장
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + originalFileName, e);
        }

        Attachment attachment = new Attachment();
        attachment.setOriginalFileName(originalFileName);
        attachment.setStoredFileName(storedFileName);

        return attachment;
    }
}
