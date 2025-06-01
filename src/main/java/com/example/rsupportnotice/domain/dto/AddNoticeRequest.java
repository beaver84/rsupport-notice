package com.example.rsupportnotice.domain.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public class AddNoticeRequest {

    @Getter
    @Setter
    public static class CreateRequest {
        @NotNull
        private NoticeInfo notice;

        @Getter
        @Setter
        public static class NoticeInfo {
            @NotBlank
            private String title;

            @NotBlank
            private String content;

            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate;
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate;
            List<MultipartFile> files;
        }
    }
}
