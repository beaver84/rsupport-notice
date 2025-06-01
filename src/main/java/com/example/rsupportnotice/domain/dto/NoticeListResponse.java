package com.example.rsupportnotice.domain.dto;

import java.time.LocalDateTime;

public record NoticeListResponse(
        String title,
        boolean hasAttachment,
        LocalDateTime createdAt,
        Long viewCount,
        String author
) {}
