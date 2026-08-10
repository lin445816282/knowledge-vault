package com.kv.core.service;

import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.common.exception.BusinessException;
import com.kv.core.entity.Knowledge;
import com.kv.core.repository.KnowledgeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeService {

    private final KnowledgeRepository knowledgeRepository;

    public List<Knowledge> list(PageRequest request) {
        return null;
    }

    public Knowledge getById(Long id) {
        return null;
    }

    @Transactional
    public Knowledge create(Knowledge entity) {
        return null;
    }

    @Transactional
    public Knowledge update(Long id, Knowledge entity) {
        return null;
    }

    @Transactional
    public void delete(Long id) {
    }
}
