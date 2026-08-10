package com.kv.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * AI 结构化服务 — 异步调用 DeepSeek API
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    @Async
    public CompletableFuture<Map<String, Object>> structurize(String rawText) {
        log.info("AI结构化任务开始...");
        // TODO: 调用 DeepSeek API 结构化文本
        // 返回标准化结构: title, industry, category, difficulty, audience, summary, principle, steps, pitfalls, experience, tags

        Map<String, Object> result = new HashMap<>();
        result.put("status", "PROCESSING");
        result.put("rawText", rawText.substring(0, Math.min(100, rawText.length())) + "...");

        try {
            Thread.sleep(2000); // 模拟AI处理
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("AI结构化任务被中断", e);
            result.put("status", "FAILED");
            result.put("error", "任务被中断");
            return CompletableFuture.completedFuture(result);
        }

        result.put("status", "COMPLETED");
        result.put("title", "自动生成标题");
        result.put("summary", "AI生成的摘要");
        result.put("difficulty", "中级");
        result.put("tags", "标签1,标签2");

        log.info("AI结构化任务完成");
        return CompletableFuture.completedFuture(result);
    }
}
