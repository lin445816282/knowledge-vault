package com.kv.core.controller;

import com.kv.common.dto.ApiResponse;
import com.kv.common.dto.PageRequest;
import com.kv.core.entity.Blacklist;
import com.kv.core.service.BlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/blacklist")
@RequiredArgsConstructor
public class BlacklistController {

    private final BlacklistService blacklistService;

    @GetMapping
    public ApiResponse<List<Blacklist>> list(PageRequest pageRequest) {
        log.info("GET /api/v1/blacklist - list blacklist entries, page={}, size={}", pageRequest.getPage(), pageRequest.getSize());
        return ApiResponse.ok(blacklistService.list(pageRequest));
    }

    @GetMapping("/{id}")
    public ApiResponse<Blacklist> getById(@PathVariable Long id) {
        log.info("GET /api/v1/blacklist/{}", id);
        return ApiResponse.ok(blacklistService.getById(id));
    }

    @PostMapping
    public ApiResponse<Blacklist> create(@RequestBody Blacklist blacklist) {
        log.info("POST /api/v1/blacklist - create blacklist entry");
        return ApiResponse.ok(blacklistService.create(blacklist));
    }

    @PutMapping("/{id}")
    public ApiResponse<Blacklist> update(@PathVariable Long id, @RequestBody Blacklist blacklist) {
        log.info("PUT /api/v1/blacklist/{} - update blacklist entry", id);
        return ApiResponse.ok(blacklistService.update(id, blacklist));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/v1/blacklist/{}", id);
        blacklistService.delete(id);
        return ApiResponse.ok();
    }
}
