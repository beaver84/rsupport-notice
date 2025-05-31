package com.example.rsupportnotice.controller;

import com.example.rsupportnotice.domain.dto.AddNoticeRequest;
import com.example.rsupportnotice.domain.dto.AddNoticeResponse;
import com.example.rsupportnotice.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping("/students")
    public ResponseEntity<AddNoticeResponse> createNotice(
            @RequestBody @Valid AddNoticeRequest.CreateRequest request
    ) {
        noticeService.createNotice(request.getNotice());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AddNoticeResponse.builder().build());
    }
}

