package com.kv.core.service;

import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.common.exception.BusinessException;
import com.kv.core.entity.Blacklist;
import com.kv.core.repository.BlacklistRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final BlacklistRepository blacklistRepository;

    public List<Blacklist> list(PageRequest request) {
        return null;
    }

    public Blacklist getById(Long id) {
        return null;
    }

    @Transactional
    public Blacklist create(Blacklist entity) {
        return null;
    }

    @Transactional
    public Blacklist update(Long id, Blacklist entity) {
        return null;
    }

    @Transactional
    public void delete(Long id) {
    }
}
