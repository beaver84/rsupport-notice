package com.example.rsupportnotice.config;

import com.example.rsupportnotice.config.exception.AlreadyExistPhoneNumberException;
import com.example.rsupportnotice.domain.dto.AddNoticeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AddNoticeResponse> handleValidationExceptions(
    ) {
        return ResponseEntity.badRequest()
                .body(StudentBuildErrorResponse("BAD_REQUEST_BODY", "올바르지 않은 인수입니다."));
    }

    @ExceptionHandler(AlreadyExistPhoneNumberException.class)
    public ResponseEntity<AddNoticeResponse> handleAlreadyExistStudent(
            AlreadyExistPhoneNumberException ex
    ) {
        return ResponseEntity.badRequest()
                .body(StudentBuildErrorResponse("ALREADY_EXIST_STUDENT", ex.getMessage()));
    }

    private AddNoticeResponse StudentBuildErrorResponse(String code, String message) {
        return AddNoticeResponse.builder()
                .error(AddNoticeResponse.ErrorInfo.builder()
                        .code(code)
                        .message(message)
                        .build())
                .build();
    }

}

