package com.kv.core.controller;

import com.kv.common.dto.ApiResponse;
import com.kv.common.dto.PageRequest;
import com.kv.core.entity.Knowledge;
import com.kv.core.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    @GetMapping
    public ApiResponse<List<Knowledge>> list(PageRequest pageRequest) {
        log.info("GET /api/v1/knowledge - list knowledge, page={}, size={}", pageRequest.getPage(), pageRequest.getSize());
        return ApiResponse.ok(knowledgeService.list(pageRequest));
    }

    @GetMapping("/{id}")
    public ApiResponse<Knowledge> getById(@PathVariable Long id) {
        log.info("GET /api/v1/knowledge/{}", id);
        return ApiResponse.ok(knowledgeService.getById(id));
    }

    @PostMapping
    public ApiResponse<Knowledge> create(@RequestBody Knowledge knowledge) {
        log.info("POST /api/v1/knowledge - create knowledge");
        return ApiResponse.ok(knowledgeService.create(knowledge));
    }

    @PutMapping("/{id}")
    public ApiResponse<Knowledge> update(@PathVariable Long id, @RequestBody Knowledge knowledge) {
        log.info("PUT /api/v1/knowledge/{} - update knowledge", id);
        return ApiResponse.ok(knowledgeService.update(id, knowledge));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/v1/knowledge/{}", id);
        knowledgeService.delete(id);
        return ApiResponse.ok();
    }
}
