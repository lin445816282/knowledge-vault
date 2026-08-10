package com.kv.core.service;

import com.kv.common.constant.KnowledgeStatus;
import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.common.exception.BusinessException;
import com.kv.core.entity.Knowledge;
import com.kv.core.entity.Order;
import com.kv.core.repository.KnowledgeRepository;
import com.kv.core.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private static final BigDecimal PLATFORM_FEE_RATE = new BigDecimal("0.20");

    private final OrderRepository orderRepository;
    private final KnowledgeRepository knowledgeRepository;
    private final BalanceService balanceService;

    // ──────────────────────────────────────────────
    // Core business methods
    // ──────────────────────────────────────────────

    /**
     * Create a pending order for purchasing knowledge.
     */
    @Transactional
    public Order createOrder(Long buyerId, Long knowledgeId) {
        Knowledge knowledge = knowledgeRepository.findById(knowledgeId)
                .orElseThrow(() -> new BusinessException(404, "知识不存在"));

        // Only PUBLIC or SOLD knowledge can be purchased
        if (knowledge.getStatus() != KnowledgeStatus.PUBLIC && knowledge.getStatus() != KnowledgeStatus.SOLD) {
            throw new BusinessException(400, "该知识暂不支持购买");
        }

        // Must have a price
        if (knowledge.getPrice() == null || knowledge.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "知识价格无效");
        }

        // Buyer cannot be the seller
        if (buyerId.equals(knowledge.getUserId())) {
            throw new BusinessException(400, "不能购买自己的知识");
        }

        BigDecimal platformFee = knowledge.getPrice().multiply(PLATFORM_FEE_RATE)
                .setScale(2, RoundingMode.HALF_UP);

        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setSellerId(knowledge.getUserId());
        order.setKnowledgeId(knowledgeId);
        order.setAmount(knowledge.getPrice());
        order.setPlatformFee(platformFee);
        order.setStatus("PENDING");

        Order saved = orderRepository.save(order);
        log.info("订单创建成功: orderId={}, buyerId={}, knowledgeId={}, amount={}",
                saved.getId(), buyerId, knowledgeId, knowledge.getPrice());
        return saved;
    }

    /**
     * Pay a pending order — deducts buyer balance.
     */
    @Transactional
    public Order payOrder(Long orderId, Long buyerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(404, "订单不存在"));

        if (!"PENDING".equals(order.getStatus())) {
            throw new BusinessException(400, "订单状态不允许支付");
        }

        if (!buyerId.equals(order.getBuyerId())) {
            throw new BusinessException(403, "无权操作此订单");
        }

        // Deduct from buyer balance
        balanceService.consume(buyerId, order.getAmount(), orderId,
                "购买知识 #" + order.getKnowledgeId());

        order.setStatus("PAID");
        Order saved = orderRepository.save(order);
        log.info("订单支付成功: orderId={}, buyerId={}", orderId, buyerId);
        return saved;
    }

    /**
     * Cancel a pending order.
     */
    @Transactional
    public Order cancelOrder(Long orderId, Long buyerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(404, "订单不存在"));

        if (!"PENDING".equals(order.getStatus())) {
            throw new BusinessException(400, "仅待支付订单可取消");
        }

        if (!buyerId.equals(order.getBuyerId())) {
            throw new BusinessException(403, "无权操作此订单");
        }

        order.setStatus("CANCELLED");
        Order saved = orderRepository.save(order);
        log.info("订单已取消: orderId={}", orderId);
        return saved;
    }

    // ──────────────────────────────────────────────
    // Query methods
    // ──────────────────────────────────────────────

    /**
     * Get orders made by a user (as buyer).
     */
    public PageResponse<Order> getMyOrders(Long userId, PageRequest request) {
        Pageable pageable = toPageable(request);
        Page<Order> page = orderRepository.findByBuyerId(userId, pageable);
        return toPageResponse(page);
    }

    /**
     * Get sales made by a user (as seller).
     */
    public PageResponse<Order> getMySales(Long userId, PageRequest request) {
        Pageable pageable = toPageable(request);
        Page<Order> page = orderRepository.findBySellerId(userId, pageable);
        return toPageResponse(page);
    }

    // ──────────────────────────────────────────────
    // Skeleton CRUD methods (kept from original)
    // ──────────────────────────────────────────────

    public List<Order> list(PageRequest request) {
        return orderRepository.findAll();
    }

    public Order getById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "订单不存在"));
    }

    @Transactional
    public Order create(Order entity) {
        return orderRepository.save(entity);
    }

    @Transactional
    public Order update(Long id, Order entity) {
        Order existing = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "订单不存在"));
        existing.setBuyerId(entity.getBuyerId());
        existing.setSellerId(entity.getSellerId());
        existing.setKnowledgeId(entity.getKnowledgeId());
        existing.setAmount(entity.getAmount());
        existing.setPlatformFee(entity.getPlatformFee());
        existing.setStatus(entity.getStatus());
        return orderRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new BusinessException(404, "订单不存在");
        }
        orderRepository.deleteById(id);
    }

    // ──────────────────────────────────────────────
    // Private helpers
    // ──────────────────────────────────────────────

    private static Pageable toPageable(PageRequest request) {
        int page = Math.max(request.getPage() - 1, 0);
        int size = Math.max(request.getSize(), 1);
        return org.springframework.data.domain.PageRequest.of(page, size);
    }

    private static <T> PageResponse<T> toPageResponse(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getTotalElements(),
                page.getNumber() + 1,
                page.getSize()
        );
    }
}
