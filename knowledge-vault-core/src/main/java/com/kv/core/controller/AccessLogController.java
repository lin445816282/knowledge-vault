package com.kv.core.controller;

import com.kv.common.dto.ApiResponse;
import com.kv.common.dto.PageRequest;
import com.kv.core.entity.AccessLog;
import com.kv.core.service.AccessLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
public class AccessLogController {

    private final AccessLogService accessLogService;

    @GetMapping
    public ApiResponse<List<AccessLog>> list(PageRequest pageRequest) {
        log.info("GET /api/v1/logs - list access logs, page={}, size={}", pageRequest.getPage(), pageRequest.getSize());
        return ApiResponse.ok(accessLogService.list(pageRequest));
    }

    @GetMapping("/{id}")
    public ApiResponse<AccessLog> getById(@PathVariable Long id) {
        log.info("GET /api/v1/logs/{}", id);
        return ApiResponse.ok(accessLogService.getById(id));
    }
}
