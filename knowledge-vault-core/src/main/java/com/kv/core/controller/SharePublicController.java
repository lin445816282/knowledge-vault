package com.kv.core.controller;

import com.kv.common.dto.ApiResponse;
import com.kv.core.entity.Knowledge;
import com.kv.core.service.ShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 公开分享接口（无需认证）。
 * 路径: GET /api/v1/public/share/{token}
 * 需在 SecurityConfig 中配置 .permitAll()
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/public/share")
@RequiredArgsConstructor
public class SharePublicController {

    private final ShareService shareService;

    /**
     * 通过分享令牌访问知识（公开入口）。
     * 返回知识摘要信息，不包含完整加密内容。
     */
    @GetMapping("/{token}")
    public ApiResponse<Knowledge> access(@PathVariable String token) {
        log.info("GET /api/v1/public/share/{} - public access", token);
        Knowledge knowledge = shareService.access(token);

        // 清除敏感字段，仅返回公开信息
        knowledge.setContentEncrypted(null);
        knowledge.setEncryptionKey(null);

        return ApiResponse.ok(knowledge);
    }
}
