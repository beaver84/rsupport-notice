package com.example.rsupportnotice.controller;

import com.example.rsupportnotice.domain.dto.NoticeResponse;
import com.example.rsupportnotice.domain.entity.Attachment;
import com.example.rsupportnotice.domain.entity.Notice;
import com.example.rsupportnotice.service.FileStorageService;
import com.example.rsupportnotice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final FileStorageService fileStorageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NoticeResponse> createNotice(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam("files") List<MultipartFile> files
    ) {
        List<Attachment> attachments = files.stream()
                .map(fileStorageService::storeFile)
                .collect(Collectors.toList());

        Notice savedNotice = noticeService.createNotice(title, content, startDate, endDate, attachments);
        return ResponseEntity.status(HttpStatus.CREATED).body(NoticeResponse.builder().build());
    }
}

