package com.kv.core.controller;

import com.kv.common.dto.ApiResponse;
import com.kv.common.dto.PageRequest;
import com.kv.core.entity.ShareLog;
import com.kv.core.service.ShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/shares")
@RequiredArgsConstructor
public class ShareController {

    private final ShareService shareService;

    @GetMapping
    public ApiResponse<List<ShareLog>> list(PageRequest pageRequest) {
        log.info("GET /api/v1/shares - list shares, page={}, size={}", pageRequest.getPage(), pageRequest.getSize());
        return ApiResponse.ok(shareService.list(pageRequest));
    }

    @GetMapping("/{id}")
    public ApiResponse<ShareLog> getById(@PathVariable Long id) {
        log.info("GET /api/v1/shares/{}", id);
        return ApiResponse.ok(shareService.getById(id));
    }

    @PostMapping
    public ApiResponse<ShareLog> create(@RequestBody ShareLog shareLog) {
        log.info("POST /api/v1/shares - create share");
        return ApiResponse.ok(shareService.create(shareLog));
    }

    @PutMapping("/{id}")
    public ApiResponse<ShareLog> update(@PathVariable Long id, @RequestBody ShareLog shareLog) {
        log.info("PUT /api/v1/shares/{} - update share", id);
        return ApiResponse.ok(shareService.update(id, shareLog));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/v1/shares/{}", id);
        shareService.delete(id);
        return ApiResponse.ok();
    }
}
