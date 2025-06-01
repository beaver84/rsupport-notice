package com.example.rsupportnotice.service;

import com.example.rsupportnotice.domain.entity.Attachment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class FileStorageService {
    public Attachment storeFile(MultipartFile file) {
        Attachment attachment = new Attachment();
        attachment.setOriginalFileName(file.getOriginalFilename());
        attachment.setStoredFileName(UUID.randomUUID() + "_" + file.getOriginalFilename());
        return attachment;
    }
}
