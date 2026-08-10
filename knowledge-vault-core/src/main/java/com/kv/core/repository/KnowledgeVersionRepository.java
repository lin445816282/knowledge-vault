package com.kv.core.repository;

import com.kv.core.entity.KnowledgeVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeVersionRepository extends JpaRepository<KnowledgeVersion, Long> {

    List<KnowledgeVersion> findByKnowledgeIdOrderByVersionNumDesc(Long knowledgeId);
}
