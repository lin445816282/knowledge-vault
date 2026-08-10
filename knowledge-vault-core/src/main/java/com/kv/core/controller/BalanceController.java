package com.kv.core.controller;

import com.kv.common.dto.ApiResponse;
import com.kv.common.dto.PageRequest;
import com.kv.core.entity.BalanceLog;
import com.kv.core.service.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/balance")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping
    public ApiResponse<List<BalanceLog>> list(PageRequest pageRequest) {
        log.info("GET /api/v1/balance - list balance logs, page={}, size={}", pageRequest.getPage(), pageRequest.getSize());
        return ApiResponse.ok(balanceService.list(pageRequest));
    }

    @GetMapping("/{id}")
    public ApiResponse<BalanceLog> getById(@PathVariable Long id) {
        log.info("GET /api/v1/balance/{}", id);
        return ApiResponse.ok(balanceService.getById(id));
    }

    @PostMapping
    public ApiResponse<BalanceLog> create(@RequestBody BalanceLog balanceLog) {
        log.info("POST /api/v1/balance - create balance log");
        return ApiResponse.ok(balanceService.create(balanceLog));
    }

    @PutMapping("/{id}")
    public ApiResponse<BalanceLog> update(@PathVariable Long id, @RequestBody BalanceLog balanceLog) {
        log.info("PUT /api/v1/balance/{} - update balance log", id);
        return ApiResponse.ok(balanceService.update(id, balanceLog));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/v1/balance/{}", id);
        balanceService.delete(id);
        return ApiResponse.ok();
    }
}
