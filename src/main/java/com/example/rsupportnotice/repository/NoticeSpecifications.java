package com.example.rsupportnotice.repository;

import com.example.rsupportnotice.domain.entity.Notice;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NoticeSpecifications {
    public static Specification<Notice> containsKeyword(String keyword, String searchType) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;
            String like = "%" + keyword + "%";
            if ("title".equalsIgnoreCase(searchType)) {
                return cb.like(root.get("title"), like);
            } else { // 제목+내용
                return cb.or(
                        cb.like(root.get("title"), like),
                        cb.like(root.get("content"), like)
                );
            }
        };
    }

    public static Specification<Notice> betweenCreatedAt(LocalDate start, LocalDate end) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (start != null) predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), start.atStartOfDay()));
            if (end != null) predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), end.atTime(23, 59, 59)));
            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
