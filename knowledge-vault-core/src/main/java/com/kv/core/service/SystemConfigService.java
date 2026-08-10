package com.kv.core.service;

import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.common.exception.BusinessException;
import com.kv.core.entity.SystemConfig;
import com.kv.core.repository.SystemConfigRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemConfigService {

    private final SystemConfigRepository systemConfigRepository;

    public List<SystemConfig> list(PageRequest request) {
        return null;
    }

    public SystemConfig getById(Long id) {
        return null;
    }

    @Transactional
    public SystemConfig create(SystemConfig entity) {
        return null;
    }

    @Transactional
    public SystemConfig update(Long id, SystemConfig entity) {
        return null;
    }

    @Transactional
    public void delete(Long id) {
    }
}
