package com.example.rsupportnotice.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class NoticeSearchCondition {

    private String keyword; // 검색어
    private String searchType; // "title+content" 또는 "title"
    private LocalDate startDate; // 등록 시작일
    private LocalDate endDate;   // 등록 종료일
}
