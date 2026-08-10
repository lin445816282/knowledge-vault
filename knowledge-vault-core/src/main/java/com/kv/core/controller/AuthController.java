package com.kv.core.controller;

import com.kv.common.dto.ApiResponse;
import com.kv.common.exception.BusinessException;
import com.kv.core.dto.*;
import com.kv.core.entity.User;
import com.kv.core.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<?> register(@Valid @RequestBody RegisterRequest req) {
        log.info("POST /api/v1/auth/register - username={}", req.getUsername());
        try {
            User u = authService.register(req);
            return ApiResponse.ok(u);
        } catch (BusinessException e) {
            return ApiResponse.fail(e.getCode(), e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@Valid @RequestBody LoginRequest req) {
        log.info("POST /api/v1/auth/login - username={}", req.getUsername());
        try {
            LoginResponse resp = authService.login(req);
            return ApiResponse.ok(resp);
        } catch (BusinessException e) {
            return ApiResponse.fail(e.getCode(), e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ApiResponse<?> refresh() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = (Long) principal;
        log.info("POST /api/v1/auth/refresh - userId={}", userId);
        LoginResponse resp = authService.refreshToken(userId);
        return ApiResponse.ok(resp);
    }
}
