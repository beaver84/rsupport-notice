package com.example.rsupportnotice.domain.dto;

public record AttachmentResponse(
        String originalFileName,
        String downloadUrl
) {}