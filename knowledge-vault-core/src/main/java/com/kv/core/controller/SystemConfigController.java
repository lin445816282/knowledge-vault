package com.kv.core.controller;

import com.kv.common.dto.ApiResponse;
import com.kv.common.dto.PageRequest;
import com.kv.core.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/system/configs")
@RequiredArgsConstructor
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    @GetMapping
    public ApiResponse<?> listAll(PageRequest pageRequest) {
        log.info("GET /api/v1/system/configs");
        return ApiResponse.ok(systemConfigService.listAll(pageRequest));
    }

    @GetMapping("/{key}")
    public ApiResponse<String> get(@PathVariable String key) {
        log.info("GET /api/v1/system/configs/{}", key);
        return ApiResponse.ok(systemConfigService.get(key));
    }

    @PutMapping("/{key}")
    public ApiResponse<?> set(@PathVariable String key, @RequestBody Map<String, String> body) {
        log.info("PUT /api/v1/system/configs/{}", key);
        systemConfigService.set(key, body.get("value"), body.getOrDefault("description", ""));
        return ApiResponse.ok();
    }
}
