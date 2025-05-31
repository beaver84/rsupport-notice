package com.example.rsupportnotice.service;

import com.example.rsupportnotice.domain.dto.AddNoticeRequest;
import com.example.rsupportnotice.domain.entity.Notice;
import com.example.rsupportnotice.repository.NoticeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional
    public void createNotice(AddNoticeRequest.CreateRequest.NoticeInfo request) {
        Notice notice = Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .noticeStartDate(request.getNoticeStartDate())
                .noticeEndDate(request.getNoticeEndDate())
                .build();

        noticeRepository.save(notice);
    }
}
