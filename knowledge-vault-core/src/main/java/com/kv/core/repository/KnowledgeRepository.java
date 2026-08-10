package com.kv.core.repository;

import com.kv.common.constant.KnowledgeStatus;
import com.kv.core.entity.Knowledge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeRepository extends JpaRepository<Knowledge, Long> {

    List<Knowledge> findByUserId(Long userId);

    List<Knowledge> findByCategoryId(Long categoryId);

    List<Knowledge> findByStatus(KnowledgeStatus status);

    List<Knowledge> findByUserIdAndStatusNot(Long userId, KnowledgeStatus status);

    // Pageable variants for paginated queries
    Page<Knowledge> findByStatus(KnowledgeStatus status, Pageable pageable);

    Page<Knowledge> findByUserId(Long userId, Pageable pageable);

    Page<Knowledge> findByTitleContainingOrTagsContaining(String title, String tags, Pageable pageable);
}
