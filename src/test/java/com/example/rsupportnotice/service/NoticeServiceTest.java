package com.example.rsupportnotice.service;

import com.example.rsupportnotice.domain.dto.NoticeDetailResponse;
import com.example.rsupportnotice.domain.dto.NoticeListResponse;
import com.example.rsupportnotice.domain.dto.NoticeSearchCondition;
import com.example.rsupportnotice.domain.entity.Attachment;
import com.example.rsupportnotice.domain.entity.Notice;
import com.example.rsupportnotice.repository.NoticeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private NoticeService noticeService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private RedisConnectionFactory redisConnectionFactory;

    @Mock
    private RedisConnection redisConnection;

    @Test
    void testGetNoticeDetail_RedisAvailable() {
        // Given
        Long noticeId = 1L;
        Notice notice = createSampleNotice(noticeId);

        when(noticeRepository.findByIdWithAttachments(noticeId)).thenReturn(Optional.of(notice));
        when(redisConnectionFactory.getConnection()).thenReturn(redisConnection);
        when(redisConnection.isClosed()).thenReturn(false);

        ZSetOperations<String, String> zSetOperations = mock(ZSetOperations.class);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.score(anyString(), anyString())).thenReturn(10.0);

        // When
        NoticeDetailResponse response = noticeService.getNoticeDetail(noticeId);

        // Then
        assertEquals("Test Title", response.title());
        assertEquals(10L, response.viewCount()); // Redis 값 10 + DB 값 0
        assertEquals(1, response.attachments().size());
        verify(redisTemplate.opsForZSet()).incrementScore("notice:views", String.valueOf(noticeId), 1);
    }

    @Test
    void testCreateNoticeWithNoFile() {
        // Given
        String title = "Test Title";
        String content = "Test Content";
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusHours(2);

        Notice notice = new Notice(title, content, startDate, endDate, LocalDateTime.now(), "admin");

        Attachment attachment = new Attachment();
        attachment.setNotice(notice);
        attachment.setOriginalFileName(null);
        attachment.setStoredFileName(null);

        when(noticeRepository.save(any(Notice.class))).thenReturn(notice);

        // When
        Notice result = noticeService.createNotice(
                "Test Title", "Test Content", LocalDateTime.now(), LocalDateTime.now().plusDays(7),
                Collections.emptyList()
        );

        // Then
        assertEquals("Test Title", result.getTitle());
        verify(noticeRepository, times(1)).save(any(Notice.class));
    }

    @Test
    void testCreateNoticeWithFiles() {
        // Given
        String title = "Test Title";
        String content = "Test Content";
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(7);

        // Mock 파일 생성
        MockMultipartFile mockFile = new MockMultipartFile(
                "files",
                "test.txt",
                "text/plain",
                "file content".getBytes()
        );
        List<MultipartFile> files = Collections.singletonList(mockFile);

        // Mock Attachment 반환값 설정
        Attachment attachment = new Attachment();
        attachment.setOriginalFileName("test.txt");
        attachment.setStoredFileName("uuid_test.txt");

        Notice notice = new Notice(title, content, startDate, endDate, LocalDateTime.now(), "admin");

        when(fileStorageService.storeFile(any(MultipartFile.class))).thenReturn(attachment);
        when(noticeRepository.save(any(Notice.class))).thenReturn(notice);

        // When
        Notice result = noticeService.createNotice(
                title, content, startDate, endDate, files
        );

        // Then
        assertEquals(title, result.getTitle());
        verify(fileStorageService, times(1)).storeFile(any(MultipartFile.class));
        verify(noticeRepository, times(1)).save(any(Notice.class));
    }

    @Test
    void getNoticeList_ShouldReturnActiveNotices() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        List<NoticeListResponse> mockResponses = Arrays.asList(
                new NoticeListResponse("제목1", true, now, 10L, "admin"),
                new NoticeListResponse("제목2", false, now, 5L, "user")
        );

        when(noticeRepository.findActiveNotices()).thenReturn(mockResponses);

        // When
        List<NoticeListResponse> result = noticeService.getNoticeList();

        // Then
        assertEquals(2, result.size());
        assertEquals("제목1", result.get(0).title());
        assertTrue(result.get(0).hasAttachment());
        verify(noticeRepository, times(1)).findActiveNotices();
    }

    @Test
    void getNoticeList_ShouldReturnEmptyListWhenNoNotices() {
        // Given
        when(noticeRepository.findActiveNotices()).thenReturn(List.of());

        // When
        List<NoticeListResponse> result = noticeService.getNoticeList();

        // Then
        assertTrue(result.isEmpty());
        verify(noticeRepository, times(1)).findActiveNotices();
    }

    private Notice createSampleNotice(Long id) {
        Notice notice = new Notice(
                "Test Title",
                "Test Content",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now(),
                "admin"
        );
        notice.setId(id);

        Attachment attachment = new Attachment("test.txt", "uuid_test.txt", notice);
        attachment.setNotice(notice);
        notice.getAttachments().add(attachment);

        return notice;
    }

    @Test
    void searchNotices_ByKeywordTitleAndContent_ReturnsMatchingNotices() {
        // Given
        NoticeSearchCondition condition = new NoticeSearchCondition();
        condition.setKeyword("test");
        condition.setSearchType("title+content");
        condition.setStartDate(null);
        condition.setEndDate(null);

        Notice notice1 = createNoticeWithAttachment("test title", "content");
        Notice notice2 = createNoticeWithAttachment("title", "test content");
        List<Notice> mockNotices = Arrays.asList(notice1, notice2);

        when(noticeRepository.findAll(any(Specification.class))).thenReturn(mockNotices);

        // When
        List<NoticeListResponse> result = noticeService.searchNotices(condition);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(r -> r.title().equals("test title")));
        assertTrue(result.stream().anyMatch(r -> r.title().equals("title")));
    }

    @Test
    void searchNotices_ByKeywordTitleOnly_ReturnsMatchingNotices() {
        // Given
        NoticeSearchCondition condition = new NoticeSearchCondition();
        condition.setKeyword("test");
        condition.setSearchType("title");
        condition.setStartDate(null);
        condition.setEndDate(null);

        Notice notice = createNoticeWithAttachment("test title", "content");
        List<Notice> mockNotices = Collections.singletonList(notice);

        when(noticeRepository.findAll(any(Specification.class))).thenReturn(mockNotices);

        // When
        List<NoticeListResponse> result = noticeService.searchNotices(condition);

        // Then
        assertEquals(1, result.size());
        assertEquals("test title", result.get(0).title());
    }

    @Test
    void searchNotices_ByDateRange_ReturnsMatchingNotices() {
        // Given
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        NoticeSearchCondition condition = new NoticeSearchCondition();
        condition.setKeyword(null);
        condition.setSearchType(null);
        condition.setStartDate(startDate);
        condition.setEndDate(endDate);

        Notice notice = createNoticeWithAttachment("title", "content");
        notice.setCreatedAt(LocalDateTime.of(2023, 6, 1, 0, 0));
        List<Notice> mockNotices = Collections.singletonList(notice);

        when(noticeRepository.findAll(any(Specification.class))).thenReturn(mockNotices);

        // When
        List<NoticeListResponse> result = noticeService.searchNotices(condition);

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).createdAt().isAfter(startDate.atStartOfDay()));
        assertTrue(result.get(0).createdAt().isBefore(endDate.plusDays(1).atStartOfDay()));
    }

    @Test
    void searchNotices_NoConditions_ReturnsAllNotices() {
        // Given
        NoticeSearchCondition condition = new NoticeSearchCondition();
        condition.setKeyword(null);
        condition.setSearchType(null);
        condition.setStartDate(null);
        condition.setEndDate(null);

        Notice notice1 = createNoticeWithAttachment("title1", "content1");
        Notice notice2 = createNoticeWithAttachment("title2", "content2");
        List<Notice> mockNotices = Arrays.asList(notice1, notice2);

        when(noticeRepository.findAll(any(Specification.class))).thenReturn(mockNotices);

        // When
        List<NoticeListResponse> result = noticeService.searchNotices(condition);

        // Then
        assertEquals(2, result.size());
    }

    private Notice createNoticeWithAttachment(String title, String content) {
        Notice notice = new Notice(
                title,
                content,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now(),
                "author"
        );
        notice.getAttachments().add(new Attachment("file.txt", "stored_file.txt", notice));
        return notice;
    }

    @Test
    void updateNotice_ShouldUpdateFieldsWithoutFiles() {
        // Given
        Long noticeId = 1L;
        Notice existingNotice = createSampleNotice("Old Title", "Old Content");
        when(noticeRepository.findByIdWithAttachments(noticeId)).thenReturn(Optional.of(existingNotice));

        String newTitle = "New Title";
        String newContent = "New Content";
        LocalDateTime newStart = LocalDateTime.now().plusDays(1);
        LocalDateTime newEnd = LocalDateTime.now().plusDays(2);

        // When
        noticeService.updateNotice(noticeId, newTitle, newContent, newStart, newEnd, null);

        // Then
        assertEquals(newTitle, existingNotice.getTitle());
        assertEquals(newContent, existingNotice.getContent());
        assertEquals(newStart, existingNotice.getStartDate());
        assertEquals(newEnd, existingNotice.getEndDate());
        verify(noticeRepository).save(existingNotice);
    }

    @Test
    void updateNotice_ShouldThrowExceptionWhenNotFound() {
        // Given
        Long invalidId = 999L;
        when(noticeRepository.findByIdWithAttachments(invalidId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            noticeService.updateNotice(invalidId, "Title", "Content",
                    LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                    Collections.emptyList());
        });
    }

    private Notice createSampleNotice(String title, String content) {
        return new Notice(
                title,
                content,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now(),
                "admin"
        );
    }

    @Test
    void deleteNotice_ShouldDeleteNoticeAndFiles() {
        // Given
        Long noticeId = 1L;
        String title = "Test Title";
        String content = "Test Content";
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusHours(2);
        Notice notice = new Notice(title, content, startDate, endDate, LocalDateTime.now(), "admin");

        Attachment attachment1 = new Attachment("file1.txt", "stored1.txt", notice);
        Attachment attachment2 = new Attachment("file2.txt", "stored2.txt", notice);
        notice.setAttachments(Arrays.asList(attachment1, attachment2));

        when(noticeRepository.findByIdWithAttachments(noticeId))
                .thenReturn(Optional.of(notice));

        // When
        noticeService.deleteNotice(noticeId);

        // Then
        verify(fileStorageService).deleteFile(attachment1);
        verify(fileStorageService).deleteFile(attachment2);
        verify(noticeRepository).delete(notice);
    }

    @Test
    void deleteNotice_ShouldThrowExceptionWhenNotFound() {
        // Given
        Long invalidId = 999L;
        when(noticeRepository.findByIdWithAttachments(invalidId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            noticeService.deleteNotice(invalidId);
        });
        verify(fileStorageService, never()).deleteFile(any());
        verify(noticeRepository, never()).delete((Notice) any());
    }
}
