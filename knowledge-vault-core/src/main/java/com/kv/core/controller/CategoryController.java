package com.kv.core.controller;

import com.kv.common.dto.ApiResponse;
import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.core.entity.Category;
import com.kv.core.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<PageResponse<Category>> list(PageRequest pageRequest) {
        log.info("GET /api/v1/categories - list categories, page={}, size={}", pageRequest.getPage(), pageRequest.getSize());
        return ApiResponse.ok(categoryService.list(pageRequest));
    }

    @GetMapping("/tree")
    public ApiResponse<List<CategoryService.TreeNode>> tree() {
        log.info("GET /api/v1/categories/tree");
        return ApiResponse.ok(categoryService.getTree());
    }

    @GetMapping("/{id}")
    public ApiResponse<Category> getById(@PathVariable Long id) {
        log.info("GET /api/v1/categories/{}", id);
        return ApiResponse.ok(categoryService.getById(id));
    }

    @PostMapping
    public ApiResponse<Category> create(@RequestBody Category category) {
        log.info("POST /api/v1/categories - create category");
        return ApiResponse.ok(categoryService.create(category));
    }

    @PutMapping("/{id}")
    public ApiResponse<Category> update(@PathVariable Long id, @RequestBody Category category) {
        log.info("PUT /api/v1/categories/{} - update category", id);
        return ApiResponse.ok(categoryService.update(id, category));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/v1/categories/{}", id);
        categoryService.delete(id);
        return ApiResponse.ok();
    }
}
