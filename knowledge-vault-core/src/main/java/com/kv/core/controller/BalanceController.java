package com.kv.core.controller;

import com.kv.common.dto.ApiResponse;
import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.core.entity.BalanceLog;
import com.kv.core.service.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/balance")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    // ──────────────────────────────────────────────
    // 辅助方法
    // ──────────────────────────────────────────────

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        throw new RuntimeException("未登录");
    }

    // ──────────────────────────────────────────────
    // 业务端点
    // ──────────────────────────────────────────────

    /**
     * 查询当前用户余额。
     */
    @GetMapping
    public ApiResponse<BigDecimal> getBalance() {
        Long userId = getCurrentUserId();
        log.info("GET /api/v1/balance - userId={}", userId);
        return ApiResponse.ok(balanceService.getBalance(userId));
    }

    /**
     * 充值。
     * Body: { "amount": 100.00, "remark": "充值备注" }
     */
    @PostMapping("/recharge")
    public ApiResponse<BigDecimal> recharge(@RequestBody Map<String, Object> body) {
        Long userId = getCurrentUserId();
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        String remark = body.containsKey("remark") ? (String) body.get("remark") : null;

        log.info("POST /api/v1/balance/recharge - userId={}, amount={}, remark={}", userId, amount, remark);
        return ApiResponse.ok(balanceService.recharge(userId, amount, remark));
    }

    /**
     * 查询余额变动日志（分页）。
     */
    @GetMapping("/logs")
    public ApiResponse<PageResponse<BalanceLog>> getLogs(PageRequest pageRequest) {
        Long userId = getCurrentUserId();
        log.info("GET /api/v1/balance/logs - userId={}, page={}, size={}", userId, pageRequest.getPage(), pageRequest.getSize());
        return ApiResponse.ok(balanceService.getLogs(userId, pageRequest));
    }
}
