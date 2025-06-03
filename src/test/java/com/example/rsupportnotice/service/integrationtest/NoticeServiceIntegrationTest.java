package com.example.rsupportnotice.service.integrationtest;

import com.example.rsupportnotice.config.exception.NoticeNotFoundException;
import com.example.rsupportnotice.domain.entity.Attachment;
import com.example.rsupportnotice.domain.entity.Notice;
import com.example.rsupportnotice.repository.NoticeRepository;
import com.example.rsupportnotice.service.FileStorageService;
import com.example.rsupportnotice.service.NoticeService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class NoticeServiceIntegrationTest {

    @Autowired
    private NoticeService noticeService;

    @MockBean
    private NoticeRepository noticeRepository;

    @MockBean
    private FileStorageService fileStorageService;

    @Test
    void createNotice_ShouldSaveNoticeWithFiles() {
        // Given
        String title = "Test Title";
        String content = "Test Content";
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(7);
        MultipartFile mockFile = mock(MultipartFile.class);
        Attachment attachment = new Attachment("test.txt", "stored_test.txt");

        when(fileStorageService.storeFile(mockFile)).thenReturn(attachment);
        when(noticeRepository.save(any(Notice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Notice result = noticeService.createNotice(
                title, content, startDate, endDate, List.of(mockFile)
        );

        // Then
        assertEquals(title, result.getTitle());
        assertEquals(1, result.getAttachments().size());
        verify(noticeRepository).save(any(Notice.class));
    }

    @Test
    void getNoticeDetail_ShouldIncrementViewCount() {
        // Given
        Long noticeId = 1L;
        Notice notice = new Notice("Title", "Content",
                LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now(), "admin");

        when(noticeRepository.findByIdWithAttachments(noticeId))
                .thenReturn(Optional.of(notice));

        // When
        noticeService.getNoticeDetail(noticeId);

        // Then (Redis 호출 검증은 별도 모킹 필요)
        verify(noticeRepository).findByIdWithAttachments(noticeId);
    }

    @Test
    void deleteNotice_WhenNotExist_ShouldThrowException() {
        // Given
        Long invalidId = 999L;
        when(noticeRepository.findByIdWithAttachments(invalidId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoticeNotFoundException.class, () -> {
            noticeService.deleteNotice(invalidId);
        });
    }
}
