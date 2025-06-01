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

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping(value = "/notices", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NoticeResponse> createNotice(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam("files") List<MultipartFile> files
    ) {
        noticeService.createNotice(title, content, startDate, endDate, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(NoticeResponse.builder().build());
    }
}

