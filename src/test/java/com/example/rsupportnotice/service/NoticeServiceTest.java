package com.example.rsupportnotice.service;

import com.example.rsupportnotice.domain.entity.Attachment;
import com.example.rsupportnotice.domain.entity.Notice;
import com.example.rsupportnotice.repository.NoticeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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
}
