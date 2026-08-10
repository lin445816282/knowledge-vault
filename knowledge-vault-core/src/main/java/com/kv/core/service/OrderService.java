package com.kv.core.service;

import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.common.exception.BusinessException;
import com.kv.core.entity.Order;
import com.kv.core.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public List<Order> list(PageRequest request) {
        return null;
    }

    public Order getById(Long id) {
        return null;
    }

    @Transactional
    public Order create(Order entity) {
        return null;
    }

    @Transactional
    public Order update(Long id, Order entity) {
        return null;
    }

    @Transactional
    public void delete(Long id) {
    }
}
