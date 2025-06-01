package com.example.rsupportnotice.service;

import com.example.rsupportnotice.domain.dto.AddNoticeRequest;
import com.example.rsupportnotice.domain.entity.Attachment;
import com.example.rsupportnotice.domain.entity.Notice;
import com.example.rsupportnotice.repository.NoticeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional
    public Notice createNotice(String title, String content, LocalDateTime startDate, LocalDateTime endDate, List<Attachment> attachments) {
        Notice notice = Notice.builder()
                .title(title)
                .content(content)
                .startDate(startDate)
                .endDate(endDate)
                .attachments(attachments)
                .build();

        return noticeRepository.save(notice);
    }
}
