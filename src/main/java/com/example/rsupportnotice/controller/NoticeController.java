package com.example.rsupportnotice.controller;

import com.example.rsupportnotice.domain.dto.NoticeDetailResponse;
import com.example.rsupportnotice.domain.dto.NoticeListResponse;
import com.example.rsupportnotice.domain.dto.NoticeResponse;
import com.example.rsupportnotice.domain.dto.NoticeSearchCondition;
import com.example.rsupportnotice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NoticeResponse> createNotice(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(value = "files", required = false) List<MultipartFile> files
    ) {
        noticeService.createNotice(title, content, startDate, endDate,
                files == null ? Collections.emptyList() : files);
        return ResponseEntity.status(HttpStatus.CREATED).body(NoticeResponse.builder().build());
    }

    @GetMapping
    public ResponseEntity<List<NoticeListResponse>> getNotices() {
        return ResponseEntity.ok(noticeService.getNoticeList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeDetailResponse> getNoticeDetail(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(noticeService.getNoticeDetail(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<NoticeListResponse>> searchNotices(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "title+content") String searchType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        NoticeSearchCondition condition = new NoticeSearchCondition();
        condition.setKeyword(keyword);
        condition.setSearchType(searchType);
        condition.setStartDate(startDate);
        condition.setEndDate(endDate);

        List<NoticeListResponse> results = noticeService.searchNotices(condition);
        return ResponseEntity.ok(results);
    }
}

