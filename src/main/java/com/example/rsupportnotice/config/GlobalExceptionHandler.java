package com.example.rsupportnotice.config;

import com.example.rsupportnotice.config.exception.NoticeNotFoundException;
import com.example.rsupportnotice.domain.dto.AddNoticeResponse;
import com.example.rsupportnotice.domain.dto.NoticeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<NoticeResponse> handleValidationExceptions(
    ) {
        return ResponseEntity.badRequest()
                .body(BuildErrorResponse("BAD_REQUEST_BODY", "올바르지 않은 인수입니다."));
    }

    private NoticeResponse BuildErrorResponse(String code, String message) {
        return NoticeResponse.builder()
                .error(NoticeResponse.ErrorInfo.builder()
                        .code(code)
                        .message(message)
                        .build())
                .build();
    }

    @ExceptionHandler(NoticeNotFoundException.class)
    public ResponseEntity<AddNoticeResponse> handleNoticeNotFound(
            NoticeNotFoundException ex
    ) {
        return ResponseEntity.badRequest()
                .body(NoticeBuildErrorResponse("NOTICE_NOT_FOUND", ex.getMessage()));
    }

    private AddNoticeResponse NoticeBuildErrorResponse(String code, String message) {
        return AddNoticeResponse.builder()
                .error(AddNoticeResponse.ErrorInfo.builder()
                        .code(code)
                        .message(message)
                        .build())
                .build();
    }

}

