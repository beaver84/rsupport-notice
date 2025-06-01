package com.example.rsupportnotice.service;

import com.example.rsupportnotice.domain.entity.Attachment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class FileStorageService {
    public Attachment storeFile(MultipartFile file) {
        // 파일 저장 로직 (예: 로컬, S3 등)
        // 저장 후 Attachment 엔티티 반환
        String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        // 실제 파일 저장 생략
        Attachment attachment = new Attachment();
        attachment.setOriginalFileName(file.getOriginalFilename());
        attachment.setStoredFileName(storedFileName);
        return attachment;
    }
}
