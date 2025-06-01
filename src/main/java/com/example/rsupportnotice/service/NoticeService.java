package com.example.rsupportnotice.service;

import com.example.rsupportnotice.domain.entity.Notice;
import com.example.rsupportnotice.repository.NoticeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public Notice createNotice(String title, String content, LocalDateTime startDate, LocalDateTime endDate, List<MultipartFile> files) {
        Notice notice = new Notice(title, content, startDate, endDate);

        // 연관 관계 메서드 사용
        files.stream()
                .map(fileStorageService::storeFile)
                .forEach(notice::addAttachment);

        return noticeRepository.save(notice);
    }
}
