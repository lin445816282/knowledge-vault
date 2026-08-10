package com.kv.core.service;

import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.common.exception.BusinessException;
import com.kv.core.entity.ShareLog;
import com.kv.core.repository.ShareLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShareService {

    private final ShareLogRepository shareLogRepository;

    public List<ShareLog> list(PageRequest request) {
        return null;
    }

    public ShareLog getById(Long id) {
        return null;
    }

    @Transactional
    public ShareLog create(ShareLog entity) {
        return null;
    }

    @Transactional
    public ShareLog update(Long id, ShareLog entity) {
        return null;
    }

    @Transactional
    public void delete(Long id) {
    }
}
