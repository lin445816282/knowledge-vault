package com.kv.core.controller;

import com.kv.common.dto.ApiResponse;
import com.kv.common.dto.PageRequest;
import com.kv.core.entity.SystemConfig;
import com.kv.core.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/system/configs")
@RequiredArgsConstructor
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    @GetMapping
    public ApiResponse<List<SystemConfig>> list(PageRequest pageRequest) {
        log.info("GET /api/v1/system/configs - list system configs, page={}, size={}", pageRequest.getPage(), pageRequest.getSize());
        return ApiResponse.ok(systemConfigService.list(pageRequest));
    }

    @GetMapping("/{id}")
    public ApiResponse<SystemConfig> getById(@PathVariable Long id) {
        log.info("GET /api/v1/system/configs/{}", id);
        return ApiResponse.ok(systemConfigService.getById(id));
    }

    @PostMapping
    public ApiResponse<SystemConfig> create(@RequestBody SystemConfig config) {
        log.info("POST /api/v1/system/configs - create system config");
        return ApiResponse.ok(systemConfigService.create(config));
    }

    @PutMapping("/{id}")
    public ApiResponse<SystemConfig> update(@PathVariable Long id, @RequestBody SystemConfig config) {
        log.info("PUT /api/v1/system/configs/{} - update system config", id);
        return ApiResponse.ok(systemConfigService.update(id, config));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/v1/system/configs/{}", id);
        systemConfigService.delete(id);
        return ApiResponse.ok();
    }
}
