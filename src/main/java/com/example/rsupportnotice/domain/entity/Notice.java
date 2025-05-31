package com.example.rsupportnotice.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column
    private LocalDateTime noticeStartDate;

    @Column
    private LocalDateTime noticeEndDate;

    @Column
    private File fileName;

    @Builder
    public Notice(long id, String title, String content, LocalDateTime noticeStartDate, LocalDateTime noticeEndDate, File fileName) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.noticeStartDate = noticeStartDate;
        this.noticeEndDate = noticeEndDate;
        this.fileName = fileName;
    }
}