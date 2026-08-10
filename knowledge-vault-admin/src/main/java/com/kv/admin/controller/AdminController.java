package com.kv.admin.controller;

import com.kv.admin.service.AdminService;
import com.kv.common.dto.ApiResponse;
import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.core.entity.Blacklist;
import com.kv.core.entity.Knowledge;
import com.kv.core.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员后台 API
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> dashboard() {
        return ApiResponse.ok(adminService.getDashboard());
    }

    @GetMapping("/users")
    public ApiResponse<PageResponse<User>> users(@ModelAttribute PageRequest pageRequest) {
        return ApiResponse.ok(adminService.listUsers(pageRequest));
    }

    @PutMapping("/users/{id}/freeze")
    public ApiResponse<User> toggleFreeze(@PathVariable Long id) {
        return ApiResponse.ok(adminService.toggleUserFreeze(id));
    }

    @GetMapping("/knowledge")
    public ApiResponse<PageResponse<Knowledge>> knowledge(@ModelAttribute PageRequest pageRequest) {
        return ApiResponse.ok(adminService.listKnowledge(pageRequest));
    }

    @DeleteMapping("/knowledge/{id}")
    public ApiResponse<Void> removeKnowledge(@PathVariable Long id) {
        adminService.removeKnowledge(id);
        return ApiResponse.ok();
    }

    @GetMapping("/logs/stats")
    public ApiResponse<Map<String, Long>> accessStats() {
        return ApiResponse.ok(adminService.getAccessStats());
    }

    @GetMapping("/blacklist")
    public ApiResponse<PageResponse<Blacklist>> blacklist(@ModelAttribute PageRequest pageRequest) {
        return ApiResponse.ok(adminService.getBlacklist(pageRequest));
    }
}
