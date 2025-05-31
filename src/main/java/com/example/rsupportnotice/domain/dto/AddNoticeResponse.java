package com.example.rsupportnotice.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddNoticeResponse {
    private Object data;
    private AddNoticeResponse.ErrorInfo error;

    @Builder
    @Getter
    public static class ErrorInfo {
        private String code;
        private String message;
    }
}
