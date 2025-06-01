package com.example.rsupportnotice.config;

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

}

