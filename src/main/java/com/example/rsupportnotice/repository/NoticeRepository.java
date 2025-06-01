package com.example.rsupportnotice.repository;

import com.example.rsupportnotice.domain.dto.NoticeListResponse;
import com.example.rsupportnotice.domain.entity.Notice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long>, JpaSpecificationExecutor<Notice> {
    // 목록 조회
    @Query("SELECT new com.example.rsupportnotice.domain.dto.NoticeListResponse(" +
            "n.title, " +
            "CASE WHEN SIZE(n.attachments) > 0 THEN true ELSE false END, " +
            "n.createdAt, " +
            "n.viewCount, " +
            "n.author) " +
            "FROM Notice n ")
    List<NoticeListResponse> findActiveNotices();

    // 상세 조회 (EntityGraph로 첨부파일 한번에 조회)
    @EntityGraph(attributePaths = "attachments")
    @Query("SELECT n FROM Notice n WHERE n.id = :id")
    Optional<Notice> findByIdWithAttachments(@Param("id") Long id);
}


