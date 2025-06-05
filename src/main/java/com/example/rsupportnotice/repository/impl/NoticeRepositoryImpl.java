package com.example.rsupportnotice.repository.impl;

import com.example.rsupportnotice.domain.dto.NoticeSearchCondition;
import com.example.rsupportnotice.domain.entity.Notice;
import com.example.rsupportnotice.domain.entity.QNotice;
import com.example.rsupportnotice.repository.NoticeRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class NoticeRepositoryImpl extends QuerydslRepositorySupport implements NoticeRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QNotice notice = QNotice.notice;

    public NoticeRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Notice.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<Notice> searchNotices(NoticeSearchCondition condition, Pageable pageable) {
        JPQLQuery<Notice> query = queryFactory
                .selectFrom(notice)
                .where(
                        keywordContains(condition.getKeyword(), condition.getSearchType()),
                        createdAtBetween(condition.getStartDate(), condition.getEndDate())
                )
                .orderBy(notice.createdAt.desc());

        List<Notice> content = getQuerydsl().applyPagination(pageable, query).fetch();
        long total = query.fetchCount();
        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression keywordContains(String keyword, String searchType) {
        if (StringUtils.isEmpty(keyword)) return null;
        String likeKeyword = "%" + keyword + "%";

        return switch (searchType) {
            case "title" -> notice.title.like(likeKeyword);
            default -> notice.title.like(likeKeyword).or(notice.content.like(likeKeyword));
        };
    }

    private BooleanExpression createdAtBetween(LocalDate start, LocalDate end) {
        if (start == null && end == null) return null;
        return notice.createdAt.between(
                start != null ? start.atStartOfDay() : LocalDateTime.MIN,
                end != null ? end.atTime(23, 59, 59) : LocalDateTime.MAX
        );
    }
}
