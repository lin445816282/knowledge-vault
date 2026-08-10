package com.kv.core.service;

import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.common.exception.BusinessException;
import com.kv.core.entity.Category;
import com.kv.core.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> list(PageRequest request) {
        return null;
    }

    public Category getById(Long id) {
        return null;
    }

    @Transactional
    public Category create(Category entity) {
        return null;
    }

    @Transactional
    public Category update(Long id, Category entity) {
        return null;
    }

    @Transactional
    public void delete(Long id) {
    }
}
