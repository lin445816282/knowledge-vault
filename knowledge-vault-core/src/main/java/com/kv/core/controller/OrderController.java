package com.kv.core.controller;

import com.kv.common.dto.ApiResponse;
import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.core.entity.Order;
import com.kv.core.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

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
     * 创建订单并完成付款。
     * Body: { "knowledgeId": 1 }
     */
    @PostMapping
    public ApiResponse<?> createAndPay(@RequestBody Map<String, Object> body) {
        Long userId = getCurrentUserId();
        Long knowledgeId = Long.valueOf(body.get("knowledgeId").toString());

        log.info("POST /api/v1/orders - userId={}, knowledgeId={}", userId, knowledgeId);
        Order order = orderService.createOrder(userId, knowledgeId);
        order = orderService.payOrder(order.getId(), userId);
        return ApiResponse.ok(Map.of("orderId", order.getId(), "status", order.getStatus()));
    }

    /**
     * 取消订单。
     */
    @PostMapping("/{id}/cancel")
    public ApiResponse<Order> cancel(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        log.info("POST /api/v1/orders/{}/cancel - userId={}", id, userId);
        return ApiResponse.ok(orderService.cancelOrder(id, userId));
    }

    /**
     * 查询我的订单（作为买家）。
     */
    @GetMapping("/my")
    public ApiResponse<PageResponse<Order>> getMyOrders(PageRequest pageRequest) {
        Long userId = getCurrentUserId();
        log.info("GET /api/v1/orders/my - userId={}, page={}, size={}", userId, pageRequest.getPage(), pageRequest.getSize());
        return ApiResponse.ok(orderService.getMyOrders(userId, pageRequest));
    }

    /**
     * 查询我的销售（作为卖家）。
     */
    @GetMapping("/sales")
    public ApiResponse<PageResponse<Order>> getMySales(PageRequest pageRequest) {
        Long userId = getCurrentUserId();
        log.info("GET /api/v1/orders/sales - userId={}, page={}, size={}", userId, pageRequest.getPage(), pageRequest.getSize());
        return ApiResponse.ok(orderService.getMySales(userId, pageRequest));
    }
}
