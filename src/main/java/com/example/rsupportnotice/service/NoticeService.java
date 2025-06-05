package com.example.rsupportnotice.service;

import com.example.rsupportnotice.config.exception.NoticeNotFoundException;
import com.example.rsupportnotice.config.usercontext.UserContextHolder;
import com.example.rsupportnotice.domain.dto.*;
import com.example.rsupportnotice.domain.entity.Attachment;
import com.example.rsupportnotice.domain.entity.Notice;
import com.example.rsupportnotice.repository.NoticeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final FileStorageService fileStorageService;
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisConnectionFactory redisConnectionFactory;

    @Transactional
    public Notice createNotice(String title, String content, LocalDateTime startDate, LocalDateTime endDate, List<MultipartFile> files) {
        Notice notice = new Notice(title, content, startDate, endDate, LocalDateTime.now(), "admin");

        // ThreadLocal에서 사용자 정보 가져오기
        notice.setAuthor(UserContextHolder.getCurrentUser());

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
                .orElseThrow(() -> new NoticeNotFoundException(id));

        // 조회수 증가 (비동기 처리)
        if (isRedisAvailable()) {
            incrementViewCountAsync(notice.getId());
        }

        NoticeDetailResponse noticeDetailResponse = convertToDetailResponse(notice);
        notice.setViewCount(noticeDetailResponse.viewCount());
        return noticeDetailResponse;
    }

    private boolean isRedisAvailable() {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            return !connection.isClosed();
        } catch (Exception e) {
            log.error("Redis 연결 불가");
            return false;
        }
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
                isRedisAvailable() ? getRedisViewCount(notice.getId()) : 0L,
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

    public List<NoticeListResponse> searchNotices(NoticeSearchCondition condition, Pageable pageable) {
        Page<Notice> notices = noticeRepository.searchNotices(condition, pageable);

        return notices.getContent().stream()
                .map(notice -> new NoticeListResponse(
                        notice.getTitle(),
                        !notice.getAttachments().isEmpty(),
                        notice.getCreatedAt(),
                        notice.getViewCount(),
                        notice.getAuthor()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateNotice(Long id, String title, String content,
                                       LocalDateTime startDate, LocalDateTime endDate,
                                       List<MultipartFile> files) {
        Notice notice = noticeRepository.findByIdWithAttachments(id)
                .orElseThrow(() -> new NoticeNotFoundException(id));

        notice.setTitle(title);
        notice.setContent(content);
        notice.setStartDate(startDate);
        notice.setEndDate(endDate);

        // ThreadLocal에서 사용자 정보 가져오기
        notice.setAuthor(UserContextHolder.getCurrentUser());

        // 기존 첨부파일 삭제 및 새 첨부파일 추가
        if (files != null && !files.isEmpty()) {
            notice.clearAttachments();
            files.stream()
                    .map(fileStorageService::storeFile)
                    .forEach(notice::addAttachment);
        }

        noticeRepository.save(notice);
    }

    @Transactional
    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findByIdWithAttachments(id)
                .orElseThrow(() -> new NoticeNotFoundException(id));

        notice.getAttachments().forEach(fileStorageService::deleteFile);

        noticeRepository.delete(notice);
    }
}
