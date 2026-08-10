package com.kv.core.service;

import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.common.exception.BusinessException;
import com.kv.core.entity.KnowledgeVersion;
import com.kv.core.repository.KnowledgeVersionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeVersionService {

    private final KnowledgeVersionRepository knowledgeVersionRepository;

    public List<KnowledgeVersion> list(Long knowledgeId, PageRequest request) {
        return null;
    }

    public KnowledgeVersion getById(Long knowledgeId, Long versionId) {
        return null;
    }

    @Transactional
    public KnowledgeVersion create(Long knowledgeId, KnowledgeVersion entity) {
        return null;
    }

    @Transactional
    public KnowledgeVersion update(Long knowledgeId, Long versionId, KnowledgeVersion entity) {
        return null;
    }

    @Transactional
    public void delete(Long knowledgeId, Long versionId) {
    }
}
