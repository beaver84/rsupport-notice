package com.example.rsupportnotice.service;

import com.example.rsupportnotice.domain.dto.AttachmentResponse;
import com.example.rsupportnotice.domain.dto.NoticeDetailResponse;
import com.example.rsupportnotice.domain.dto.NoticeListResponse;
import com.example.rsupportnotice.domain.entity.Attachment;
import com.example.rsupportnotice.domain.entity.Notice;
import com.example.rsupportnotice.repository.NoticeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final FileStorageService fileStorageService;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public Notice createNotice(String title, String content, LocalDateTime startDate, LocalDateTime endDate, List<MultipartFile> files) {
        Notice notice = new Notice(title, content, startDate, endDate);

        // 연관 관계 메서드 사용
        files.stream()
                .map(fileStorageService::storeFile)
                .forEach(notice::addAttachment);

        return noticeRepository.save(notice);
    }

    @Transactional
    public List<NoticeListResponse> getNoticeList() {
        return noticeRepository.findActiveNotices();
    }

    @Transactional
    public NoticeDetailResponse getNoticeDetail(Long id) {
        Notice notice = noticeRepository.findByIdWithAttachments(id)
                .orElseThrow(() -> new EntityNotFoundException("Notice not found"));

        // 조회수 증가 (비동기 처리)
        incrementViewCountAsync(notice.getId());

        return convertToDetailResponse(notice);
    }

    @Async
    public void incrementViewCountAsync(Long noticeId) {
        redisTemplate.opsForZSet().incrementScore("notice:views", String.valueOf(noticeId), 1);
    }

    private NoticeDetailResponse convertToDetailResponse(Notice notice) {
        return new NoticeDetailResponse(
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatedAt(),
                notice.getViewCount() + getRedisViewCount(notice.getId()),
                notice.getAuthor(),
                notice.getAttachments().stream()
                        .map(this::convertToAttachmentResponse)
                        .flatMap(Optional::stream)
                        .collect(Collectors.toList()) // downloadUrl이 빈 값이면 빈 리스트 반환
        );
    }

    private Long getRedisViewCount(Long noticeId) {
        Double score = redisTemplate.opsForZSet().score("notice:views", String.valueOf(noticeId));
        return score != null ? score.longValue() : 0L;
    }

    private Optional<AttachmentResponse> convertToAttachmentResponse(Attachment attachment) {
        String downloadUrl = (attachment.getStoredFileName() == null || attachment.getStoredFileName().isBlank())
                ? "" : "/api/attachments/" + attachment.getStoredFileName();

        // downloadUrl이 빈 값이면 Optional.empty() 반환
        if (downloadUrl.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(new AttachmentResponse(
                attachment.getOriginalFileName(),
                downloadUrl
        ));
    }
}
