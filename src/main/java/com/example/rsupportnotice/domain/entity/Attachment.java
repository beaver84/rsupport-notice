package com.example.rsupportnotice.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String originalFileName;

    @Column
    private String storedFileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    @Builder
    public Attachment(String originalFileName, String storedFileName, Notice notice) {
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.notice = notice;
    }
}
