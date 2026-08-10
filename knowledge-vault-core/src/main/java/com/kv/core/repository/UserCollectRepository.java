package com.kv.core.repository;

import com.kv.core.entity.UserCollect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCollectRepository extends JpaRepository<UserCollect, Long> {

    List<UserCollect> findByUserId(Long userId);

    Optional<UserCollect> findByUserIdAndKnowledgeId(Long userId, Long knowledgeId);

    @Modifying
    @Transactional
    void deleteByUserIdAndKnowledgeId(Long userId, Long knowledgeId);
}
