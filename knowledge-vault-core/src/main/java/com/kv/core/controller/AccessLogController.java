package com.kv.core.controller;

import com.kv.common.constant.AccessType;
import com.kv.common.dto.ApiResponse;
import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.core.entity.AccessLog;
import com.kv.core.service.AccessLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
public class AccessLogController {

    private final AccessLogService accessLogService;

    @GetMapping
    public ApiResponse<PageResponse<AccessLog>> getRecentLogs(PageRequest pageRequest) {
        log.info("GET /api/v1/logs - get recent logs, page={}, size={}", pageRequest.getPage(), pageRequest.getSize());
        return ApiResponse.ok(accessLogService.getRecentLogs(pageRequest));
    }

    @GetMapping("/stats")
    public ApiResponse<Map<AccessType, Long>> getStats() {
        log.info("GET /api/v1/logs/stats - get access stats");
        return ApiResponse.ok(accessLogService.getStats());
    }
}
