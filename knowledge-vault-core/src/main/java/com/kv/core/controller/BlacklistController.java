package com.kv.core.controller;

import com.kv.common.dto.ApiResponse;
import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.core.dto.BlockRequest;
import com.kv.core.dto.UnblockRequest;
import com.kv.core.entity.Blacklist;
import com.kv.core.service.BlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/blacklist")
@RequiredArgsConstructor
public class BlacklistController {

    private final BlacklistService blacklistService;

    @GetMapping
    public ApiResponse<PageResponse<Blacklist>> listAll(PageRequest pageRequest) {
        log.info("GET /api/v1/blacklist - list all entries, page={}, size={}", pageRequest.getPage(), pageRequest.getSize());
        return ApiResponse.ok(blacklistService.listAll(pageRequest));
    }

    @PostMapping("/block")
    public ApiResponse<Blacklist> block(@RequestBody BlockRequest request) {
        log.info("POST /api/v1/blacklist/block - type={}, value={}, reason={}, expireMinutes={}",
                request.getTargetType(), request.getTargetValue(), request.getReason(), request.getExpireMinutes());
        Blacklist result = blacklistService.block(
                request.getTargetType(),
                request.getTargetValue(),
                request.getReason(),
                request.getExpireMinutes()
        );
        return ApiResponse.ok(result);
    }

    @PostMapping("/unblock")
    public ApiResponse<Void> unblock(@RequestBody UnblockRequest request) {
        log.info("POST /api/v1/blacklist/unblock - type={}, value={}",
                request.getTargetType(), request.getTargetValue());
        blacklistService.unblock(request.getTargetType(), request.getTargetValue());
        return ApiResponse.ok();
    }

    @GetMapping("/check")
    public ApiResponse<Boolean> isBlocked(@RequestParam String value) {
        log.info("GET /api/v1/blacklist/check?value={}", value);
        return ApiResponse.ok(blacklistService.isBlocked(value));
    }
}
