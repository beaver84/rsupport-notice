package com.example.rsupportnotice.domain.dto;

import java.time.LocalDateTime;
import java.util.List;

public record NoticeDetailResponse(
        String title,
        String content,
        LocalDateTime createdAt,
        Long viewCount,
        String author,
        List<AttachmentResponse> attachments
) {}