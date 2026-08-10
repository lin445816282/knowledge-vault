package com.kv.core.controller;

import com.kv.common.dto.ApiResponse;
import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.core.entity.ShareLog;
import com.kv.core.service.ShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/shares")
@RequiredArgsConstructor
public class ShareController {

    private final ShareService shareService;

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
    // 原有骨架端点（保留兼容）
    // ──────────────────────────────────────────────

    @GetMapping("/all")
    public ApiResponse<List<ShareLog>> list(PageRequest pageRequest) {
        log.info("GET /api/v1/shares/all - list shares, page={}, size={}", pageRequest.getPage(), pageRequest.getSize());
        return ApiResponse.ok(shareService.list(pageRequest));
    }

    @GetMapping("/detail/{id}")
    public ApiResponse<ShareLog> getById(@PathVariable Long id) {
        log.info("GET /api/v1/shares/detail/{}", id);
        return ApiResponse.ok(shareService.getById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<ShareLog> update(@PathVariable Long id, @RequestBody ShareLog shareLog) {
        log.info("PUT /api/v1/shares/{} - update share", id);
        return ApiResponse.ok(shareService.update(id, shareLog));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/v1/shares/{}", id);
        shareService.delete(id);
        return ApiResponse.ok();
    }

    // ──────────────────────────────────────────────
    // 新增端点
    // ──────────────────────────────────────────────

    /**
     * 创建分享链接。
     * Body: { "knowledgeId": 1, "expireHours": 24, "deviceLimit": "" }
     */
    @PostMapping
    public ApiResponse<Map<String, String>> share(@RequestBody Map<String, Object> body) {
        Long userId = getCurrentUserId();
        Long knowledgeId = Long.valueOf(body.get("knowledgeId").toString());
        int expireHours = body.containsKey("expireHours") ? ((Number) body.get("expireHours")).intValue() : 0;
        String deviceLimit = body.containsKey("deviceLimit") ? (String) body.get("deviceLimit") : null;

        log.info("POST /api/v1/shares - create share, userId={}, knowledgeId={}, expireHours={}", userId, knowledgeId, expireHours);
        String token = shareService.share(knowledgeId, userId, expireHours, deviceLimit);
        return ApiResponse.ok(Map.of("token", token));
    }

    /**
     * 撤销分享（通过 token）。
     */
    @PostMapping("/{token}/revoke")
    public ApiResponse<Void> revoke(@PathVariable String token) {
        Long userId = getCurrentUserId();
        log.info("POST /api/v1/shares/{}/revoke - userId={}", token, userId);
        shareService.revoke(token, userId);
        return ApiResponse.ok();
    }

    /**
     * 查询我的分享列表（分页）。
     */
    @GetMapping("/my")
    public ApiResponse<PageResponse<ShareLog>> listMyShares(PageRequest pageRequest) {
        Long userId = getCurrentUserId();
        log.info("GET /api/v1/shares/my - userId={}, page={}, size={}", userId, pageRequest.getPage(), pageRequest.getSize());
        return ApiResponse.ok(shareService.listMyShares(userId, pageRequest));
    }

    /**
     * 查询某个分享的访问日志。
     */
    @GetMapping("/{token}/access-logs")
    public ApiResponse<List<ShareLog>> listAccessLogs(@PathVariable String token) {
        Long userId = getCurrentUserId();
        log.info("GET /api/v1/shares/{}/access-logs - userId={}", token, userId);
        return ApiResponse.ok(shareService.listAccessLogs(token, userId));
    }
}
