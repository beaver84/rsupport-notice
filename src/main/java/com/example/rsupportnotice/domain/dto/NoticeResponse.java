package com.example.rsupportnotice.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticeResponse {
    private Object data;
    private NoticeResponse.ErrorInfo error;

    @Builder
    @Getter
    public static class ErrorInfo {
        private String code;
        private String message;
    }
}
