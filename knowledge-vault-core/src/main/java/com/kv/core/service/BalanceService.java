package com.kv.core.service;

import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.common.exception.BusinessException;
import com.kv.core.entity.BalanceLog;
import com.kv.core.repository.BalanceLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceLogRepository balanceLogRepository;

    public List<BalanceLog> list(PageRequest request) {
        return null;
    }

    public BalanceLog getById(Long id) {
        return null;
    }

    @Transactional
    public BalanceLog create(BalanceLog entity) {
        return null;
    }

    @Transactional
    public BalanceLog update(Long id, BalanceLog entity) {
        return null;
    }

    @Transactional
    public void delete(Long id) {
    }
}
