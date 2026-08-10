package com.kv.ai.controller;

import com.kv.ai.service.AiService;
import com.kv.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI 结构化服务 API
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    /** 异步任务结果存储：taskId → 结构化结果 */
    private final ConcurrentHashMap<String, Map<String, Object>> taskStore = new ConcurrentHashMap<>();

    /**
     * 提交结构化任务
     */
    @PostMapping("/structurize")
    public ApiResponse<Map<String, String>> structurize(@RequestBody Map<String, Object> body) {
        String rawText = (String) body.getOrDefault("rawText", "");
        String taskId = UUID.randomUUID().toString();

        // 初始化任务状态
        Map<String, Object> initialStatus = new ConcurrentHashMap<>();
        initialStatus.put("status", "PENDING");
        initialStatus.put("rawText", rawText);
        taskStore.put(taskId, initialStatus);

        // 异步调用 AI 服务
        CompletableFuture<Map<String, Object>> future = aiService.structurize(rawText);
        future.thenAccept(result -> {
            taskStore.put(taskId, result);
            log.info("任务 {} 完成: status={}", taskId, result.get("status"));
        }).exceptionally(ex -> {
            log.error("任务 {} 异常: {}", taskId, ex.getMessage(), ex);
            Map<String, Object> errorResult = new ConcurrentHashMap<>();
            errorResult.put("status", "FAILED");
            errorResult.put("error", ex.getMessage());
            taskStore.put(taskId, errorResult);
            return null;
        });

        return ApiResponse.ok(Map.of("taskId", taskId, "status", "PENDING"));
    }

    /**
     * 查询任务状态
     */
    @GetMapping("/tasks/{taskId}")
    public ApiResponse<Map<String, Object>> taskStatus(@PathVariable String taskId) {
        Map<String, Object> result = taskStore.get(taskId);
        if (result == null) {
            return ApiResponse.fail(404, "任务不存在: " + taskId);
        }
        return ApiResponse.ok(result);
    }
}
