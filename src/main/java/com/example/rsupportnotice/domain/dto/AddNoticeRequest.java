package com.example.rsupportnotice.domain.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.time.LocalDateTime;

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

            @NotNull
            private LocalDateTime noticeStartDate;

            @NotNull
            private LocalDateTime noticeEndDate;

            private File fileName;
        }
    }
}
