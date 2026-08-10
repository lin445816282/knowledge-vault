package com.kv.ai.controller;

import com.kv.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * AI 结构化服务 API
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    /**
     * 提交结构化任务
     */
    @PostMapping("/structurize")
    public ApiResponse<Map<String, String>> structurize(@RequestBody Map<String, Object> body) {
        String taskId = UUID.randomUUID().toString();
        // TODO Phase 6: 异步调用 DeepSeek API
        return ApiResponse.ok(Map.of("taskId", taskId, "status", "PENDING"));
    }

    /**
     * 查询任务状态
     */
    @GetMapping("/tasks/{taskId}")
    public ApiResponse<Map<String, String>> taskStatus(@PathVariable String taskId) {
        // TODO Phase 6: 查询异步任务进度
        return ApiResponse.ok(Map.of("taskId", taskId, "status", "PENDING"));
    }
}
