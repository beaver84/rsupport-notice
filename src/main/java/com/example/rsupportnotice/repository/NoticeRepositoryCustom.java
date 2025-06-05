package com.example.rsupportnotice.repository;

import com.example.rsupportnotice.domain.dto.NoticeSearchCondition;
import com.example.rsupportnotice.domain.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeRepositoryCustom {
    Page<Notice> searchNotices(NoticeSearchCondition condition, Pageable pageable);
}
