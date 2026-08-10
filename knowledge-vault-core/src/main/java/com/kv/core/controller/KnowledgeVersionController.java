package com.kv.core.controller;

import com.kv.common.dto.ApiResponse;
import com.kv.common.dto.PageRequest;
import com.kv.core.entity.KnowledgeVersion;
import com.kv.core.service.KnowledgeVersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/knowledge/{knowledgeId}/versions")
@RequiredArgsConstructor
public class KnowledgeVersionController {

    private final KnowledgeVersionService knowledgeVersionService;

    @GetMapping
    public ApiResponse<List<KnowledgeVersion>> list(@PathVariable Long knowledgeId, PageRequest pageRequest) {
        log.info("GET /api/v1/knowledge/{}/versions - list versions, page={}, size={}", knowledgeId, pageRequest.getPage(), pageRequest.getSize());
        return ApiResponse.ok(knowledgeVersionService.list(knowledgeId, pageRequest));
    }

    @GetMapping("/{id}")
    public ApiResponse<KnowledgeVersion> getById(@PathVariable Long knowledgeId, @PathVariable Long id) {
        log.info("GET /api/v1/knowledge/{}/versions/{}", knowledgeId, id);
        return ApiResponse.ok(knowledgeVersionService.getById(knowledgeId, id));
    }

    @PostMapping
    public ApiResponse<KnowledgeVersion> create(@PathVariable Long knowledgeId, @RequestBody KnowledgeVersion version) {
        log.info("POST /api/v1/knowledge/{}/versions - create version", knowledgeId);
        return ApiResponse.ok(knowledgeVersionService.create(knowledgeId, version));
    }

    @PutMapping("/{id}")
    public ApiResponse<KnowledgeVersion> update(@PathVariable Long knowledgeId, @PathVariable Long id, @RequestBody KnowledgeVersion version) {
        log.info("PUT /api/v1/knowledge/{}/versions/{} - update version", knowledgeId, id);
        return ApiResponse.ok(knowledgeVersionService.update(knowledgeId, id, version));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long knowledgeId, @PathVariable Long id) {
        log.info("DELETE /api/v1/knowledge/{}/versions/{}", knowledgeId, id);
        knowledgeVersionService.delete(knowledgeId, id);
        return ApiResponse.ok();
    }
}
