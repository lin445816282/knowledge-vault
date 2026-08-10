package com.kv.core.controller;

import com.kv.common.dto.ApiResponse;
import com.kv.common.dto.PageRequest;
import com.kv.core.entity.User;
import com.kv.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
