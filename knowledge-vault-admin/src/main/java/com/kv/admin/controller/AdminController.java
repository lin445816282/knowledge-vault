package com.kv.admin.controller;

import com.kv.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员后台 API
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> dashboard() {
        return ApiResponse.ok(Map.of(
            "totalUsers", 0,
            "totalKnowledge", 0,
            "totalOrders", 0,
            "todayAccess", 0
        ));
    }

    @GetMapping("/users")
    public ApiResponse<List<?>> users() {
        return ApiResponse.ok(List.of());
    }

    @GetMapping("/logs")
    public ApiResponse<List<?>> logs() {
        return ApiResponse.ok(List.of());
    }

    @GetMapping("/blacklist")
    public ApiResponse<List<?>> blacklist() {
        return ApiResponse.ok(List.of());
    }
}
