package com.kv.core.service;

import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.common.exception.BusinessException;
import com.kv.core.entity.UserCollect;
import com.kv.core.repository.UserCollectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectionService {

    private final UserCollectRepository userCollectRepository;

    public List<UserCollect> list(PageRequest request) {
        return null;
    }

    public UserCollect getById(Long id) {
        return null;
    }

    @Transactional
    public UserCollect create(UserCollect entity) {
        return null;
    }

    @Transactional
    public UserCollect update(Long id, UserCollect entity) {
        return null;
    }

    @Transactional
    public void delete(Long id) {
    }
}
