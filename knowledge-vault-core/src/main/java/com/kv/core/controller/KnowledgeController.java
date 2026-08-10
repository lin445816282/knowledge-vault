package com.kv.core.controller;

import com.kv.common.dto.ApiResponse;
import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.core.entity.Knowledge;
import com.kv.core.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    // ──────────────────────────────────────────────
    // 辅助方法
    // ──────────────────────────────────────────────

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        throw new RuntimeException("未登录");
    }

    // ──────────────────────────────────────────────
    // 现有骨架端点（已接入真实 Service）
    // ──────────────────────────────────────────────

    @GetMapping
    public ApiResponse<PageResponse<Knowledge>> list(PageRequest pageRequest) {
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

    // ──────────────────────────────────────────────
    // 新增端点
    // ──────────────────────────────────────────────

    /**
     * 分页列出当前用户的知识。
     */
    @GetMapping("/my")
    public ApiResponse<PageResponse<Knowledge>> listMyKnowledge(PageRequest pageRequest) {
        Long userId = getCurrentUserId();
        log.info("GET /api/v1/knowledge/my - userId={}, page={}, size={}", userId, pageRequest.getPage(), pageRequest.getSize());
        return ApiResponse.ok(knowledgeService.listMyKnowledge(userId, pageRequest));
    }

    /**
     * 根据关键词搜索知识（匹配标题或标签）。
     */
    @GetMapping("/search")
    public ApiResponse<PageResponse<Knowledge>> search(@RequestParam String keyword, PageRequest pageRequest) {
        log.info("GET /api/v1/knowledge/search?keyword={}, page={}, size={}", keyword, pageRequest.getPage(), pageRequest.getSize());
        return ApiResponse.ok(knowledgeService.search(keyword, pageRequest));
    }

    /**
     * 增加知识浏览次数。
     */
    @PostMapping("/{id}/view")
    public ApiResponse<Void> incrementViewCount(@PathVariable Long id) {
        log.info("POST /api/v1/knowledge/{}/view", id);
        knowledgeService.incrementViewCount(id);
        return ApiResponse.ok();
    }
}
