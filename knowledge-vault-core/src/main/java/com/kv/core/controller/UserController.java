package com.kv.core.controller;

import com.kv.common.dto.ApiResponse;
import com.kv.common.dto.PageRequest;
import com.kv.core.dto.UserProfileDTO;
import com.kv.core.entity.User;
import com.kv.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<List<User>> list(PageRequest pageRequest) {
        log.info("GET /api/v1/users - list users, page={}, size={}", pageRequest.getPage(), pageRequest.getSize());
        return ApiResponse.ok(userService.list(pageRequest));
    }

    @GetMapping("/{id}")
    public ApiResponse<User> getById(@PathVariable Long id) {
        log.info("GET /api/v1/users/{}", id);
        return ApiResponse.ok(userService.getById(id));
    }

    @PostMapping
    public ApiResponse<User> create(@RequestBody User user) {
        log.info("POST /api/v1/users - create user");
        return ApiResponse.ok(userService.create(user));
    }

    @PutMapping("/{id}")
    public ApiResponse<User> update(@PathVariable Long id, @RequestBody User user) {
        log.info("PUT /api/v1/users/{} - update user", id);
        return ApiResponse.ok(userService.update(id, user));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/v1/users/{}", id);
        userService.delete(id);
        return ApiResponse.ok();
    }

    @GetMapping("/profile")
    public ApiResponse<UserProfileDTO> getProfile() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("GET /api/v1/users/profile - user={}", userId);
        return ApiResponse.ok(userService.getProfile(userId));
    }

    @PutMapping("/profile")
    public ApiResponse<UserProfileDTO> updateProfile(@RequestBody UserProfileDTO dto) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("PUT /api/v1/users/profile - user={}", userId);
        return ApiResponse.ok(userService.updateProfile(userId, dto));
    }

    @PutMapping("/password")
    public ApiResponse<?> changePassword(@RequestBody Map<String, String> body) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("PUT /api/v1/users/password - user={}", userId);
        userService.changePassword(userId, body.get("oldPassword"), body.get("newPassword"));
        return ApiResponse.ok();
    }

    @PostMapping("/topup")
    public ApiResponse<Map<String, Object>> topUp(@RequestBody Map<String, Object> body) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        log.info("POST /api/v1/users/topup - user={}, amount={}", userId, amount);
        BigDecimal newBalance = userService.topUp(userId, amount);
        return ApiResponse.ok(Map.of("balance", newBalance));
    }
}
