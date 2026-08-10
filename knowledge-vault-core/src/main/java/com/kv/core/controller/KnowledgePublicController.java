package com.kv.core.controller;

import com.kv.common.dto.ApiResponse;
import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.core.entity.Knowledge;
import com.kv.core.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公开知识接口（无需认证）。
 * 路径: GET /api/v1/public/knowledge
 * 需在 SecurityConfig 中配置 .permitAll()
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/public/knowledge")
@RequiredArgsConstructor
public class KnowledgePublicController {

    private final KnowledgeService knowledgeService;

    @GetMapping
    public ApiResponse<PageResponse<Knowledge>> listPublic(PageRequest pageRequest) {
        log.info("GET /api/v1/public/knowledge - public list, page={}, size={}", pageRequest.getPage(), pageRequest.getSize());
        return ApiResponse.ok(knowledgeService.listPublic(pageRequest));
    }
}
