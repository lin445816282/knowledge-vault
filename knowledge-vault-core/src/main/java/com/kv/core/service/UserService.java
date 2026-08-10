package com.kv.core.service;

import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.common.exception.BusinessException;
import com.kv.core.entity.User;
import com.kv.core.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> list(PageRequest request) {
        return null;
    }

    public User getById(Long id) {
        return null;
    }

    @Transactional
    public User create(User entity) {
        return null;
    }

    @Transactional
    public User update(Long id, User entity) {
        return null;
    }

    @Transactional
    public void delete(Long id) {
    }
}
