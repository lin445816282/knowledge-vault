package com.kv.core.controller;

import com.kv.common.dto.ApiResponse;
import com.kv.common.dto.PageRequest;
import com.kv.core.entity.Order;
import com.kv.core.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ApiResponse<List<Order>> list(PageRequest pageRequest) {
        log.info("GET /api/v1/orders - list orders, page={}, size={}", pageRequest.getPage(), pageRequest.getSize());
        return ApiResponse.ok(orderService.list(pageRequest));
    }

    @GetMapping("/{id}")
    public ApiResponse<Order> getById(@PathVariable Long id) {
        log.info("GET /api/v1/orders/{}", id);
        return ApiResponse.ok(orderService.getById(id));
    }

    @PostMapping
    public ApiResponse<Order> create(@RequestBody Order order) {
        log.info("POST /api/v1/orders - create order");
        return ApiResponse.ok(orderService.create(order));
    }

    @PutMapping("/{id}")
    public ApiResponse<Order> update(@PathVariable Long id, @RequestBody Order order) {
        log.info("PUT /api/v1/orders/{} - update order", id);
        return ApiResponse.ok(orderService.update(id, order));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/v1/orders/{}", id);
        orderService.delete(id);
        return ApiResponse.ok();
    }
}
