package com.example.rsupportnotice.controller;

import com.example.rsupportnotice.domain.dto.NoticeDetailResponse;
import com.example.rsupportnotice.domain.dto.NoticeListResponse;
import com.example.rsupportnotice.service.NoticeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoticeController.class)
class NoticeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NoticeService noticeService;

    @Test
    @DisplayName("공지사항 목록 조회")
    void getNoticeList() throws Exception {
        List<NoticeListResponse> mockList = List.of(
                new NoticeListResponse("제목1", true, LocalDateTime.now(), 10L, "관리자"),
                new NoticeListResponse("제목2", false, LocalDateTime.now(), 5L, "관리자")
        );
        Mockito.when(noticeService.getNoticeList()).thenReturn(mockList);

        mockMvc.perform(get("/api/notices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("제목1"))
                .andExpect(jsonPath("$[1].hasAttachment").value(false));
    }

    @Test
    @DisplayName("공지사항 상세 조회")
    void getNoticeDetail() throws Exception {
        NoticeDetailResponse mockDetail = new NoticeDetailResponse(
                "제목1", "내용1", LocalDateTime.now(), 10L, "관리자", List.of()
        );
        Mockito.when(noticeService.getNoticeDetail(1L)).thenReturn(mockDetail);

        mockMvc.perform(get("/api/notices/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("제목1"))
                .andExpect(jsonPath("$.viewCount").value(10));
    }

    @Test
    @DisplayName("공지사항 삭제")
    void deleteNotice() throws Exception {
        Mockito.doNothing().when(noticeService).deleteNotice(1L);

        mockMvc.perform(delete("/api/notices/1"))
                .andExpect(status().isNoContent());
    }
}
