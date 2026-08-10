package com.kv.core.controller;

import com.kv.common.dto.ApiResponse;
import com.kv.common.exception.BusinessException;
import com.kv.core.entity.Knowledge;
import com.kv.core.entity.KnowledgeVersion;
import com.kv.core.service.KnowledgeVersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/knowledge/{knowledgeId}/versions")
@RequiredArgsConstructor
public class KnowledgeVersionController {

    private final KnowledgeVersionService knowledgeVersionService;

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        throw new BusinessException(401, "未登录");
    }

    /**
     * 列出知识的所有版本
     */
    @GetMapping
    public ApiResponse<List<KnowledgeVersion>> listVersions(@PathVariable Long knowledgeId) {
        log.info("GET /api/v1/knowledge/{}/versions", knowledgeId);
        return ApiResponse.ok(knowledgeVersionService.listVersions(knowledgeId));
    }

    /**
     * 获取单个版本详情（含解密内容）
     */
    @GetMapping("/{versionId}")
    public ApiResponse<KnowledgeVersion> getVersion(@PathVariable Long knowledgeId,
                                                     @PathVariable Long versionId) {
        log.info("GET /api/v1/knowledge/{}/versions/{}", knowledgeId, versionId);
        return ApiResponse.ok(knowledgeVersionService.getVersion(knowledgeId, versionId));
    }

    /**
     * 回滚到指定版本
     */
    @PostMapping("/rollback")
    public ApiResponse<Knowledge> rollback(@PathVariable Long knowledgeId,
                                           @RequestParam Long versionId) {
        Long userId = getCurrentUserId();
        log.info("POST /api/v1/knowledge/{}/versions/rollback?versionId={}, userId={}",
                knowledgeId, versionId, userId);
        return ApiResponse.ok(knowledgeVersionService.rollback(knowledgeId, versionId, userId));
    }

    /**
     * 对比两个版本
     */
    @GetMapping("/diff")
    public ApiResponse<Map<String, Object>> diff(@PathVariable Long knowledgeId,
                                                  @RequestParam Long v1,
                                                  @RequestParam Long v2) {
        log.info("GET /api/v1/knowledge/{}/versions/diff?v1={}&v2={}", knowledgeId, v1, v2);
        return ApiResponse.ok(knowledgeVersionService.diff(knowledgeId, v1, v2));
    }
}
