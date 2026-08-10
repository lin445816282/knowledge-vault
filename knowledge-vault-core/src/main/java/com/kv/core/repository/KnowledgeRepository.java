package com.kv.core.repository;

import com.kv.common.constant.KnowledgeStatus;
import com.kv.core.entity.Knowledge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeRepository extends JpaRepository<Knowledge, Long> {

    List<Knowledge> findByUserId(Long userId);

    List<Knowledge> findByCategoryId(Long categoryId);

    List<Knowledge> findByStatus(KnowledgeStatus status);

    List<Knowledge> findByUserIdAndStatusNot(Long userId, KnowledgeStatus status);
}
