package com.kv.admin.service;

import com.kv.common.constant.KnowledgeStatus;
import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.common.exception.BusinessException;
import com.kv.core.entity.*;
import com.kv.core.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final KnowledgeRepository knowledgeRepository;
    private final OrderRepository orderRepository;
    private final AccessLogRepository accessLogRepository;
    private final BlacklistRepository blacklistRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // ─── Dashboard ─────────────────────────────────────────────────

    public Map<String, Object> getDashboard() {
        long totalUsers = userRepository.count();
        long totalKnowledge = knowledgeRepository.count();
        long totalOrders = orderRepository.count();

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);

        long todayAccess = entityManager.createQuery(
                "SELECT COUNT(a) FROM AccessLog a WHERE a.createdAt >= :start AND a.createdAt < :end",
                Long.class
        ).setParameter("start", todayStart).setParameter("end", todayEnd).getSingleResult();

        BigDecimal todayRevenue = entityManager.createQuery(
                "SELECT COALESCE(SUM(o.amount), 0) FROM Order o WHERE o.status = :status AND o.createdAt >= :start AND o.createdAt < :end",
                BigDecimal.class
        ).setParameter("status", "PAID")
         .setParameter("start", todayStart)
         .setParameter("end", todayEnd)
         .getSingleResult();

        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("totalUsers", totalUsers);
        dashboard.put("totalKnowledge", totalKnowledge);
        dashboard.put("totalOrders", totalOrders);
        dashboard.put("todayAccess", todayAccess);
        dashboard.put("todayRevenue", todayRevenue);
        return dashboard;
    }

    // ─── User Management ──────────────────────────────────────────

    public PageResponse<User> listUsers(PageRequest pageRequest) {
        Pageable pageable = toPageable(pageRequest);
        Page<User> page = userRepository.findAll(pageable);
        return new PageResponse<>(page.getContent(), page.getTotalElements(),
                pageRequest.getPage(), pageRequest.getSize());
    }

    @Transactional
    public User toggleUserFreeze(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        user.setFrozen(!Boolean.TRUE.equals(user.getFrozen()));
        userRepository.save(user);
        log.info("User {} freeze toggled → {}", userId, user.getFrozen());
        return user;
    }

    // ─── Knowledge Management ─────────────────────────────────────

    public PageResponse<Knowledge> listKnowledge(PageRequest pageRequest) {
        Pageable pageable = toPageable(pageRequest);
        Page<Knowledge> page = knowledgeRepository.findAll(pageable);
        return new PageResponse<>(page.getContent(), page.getTotalElements(),
                pageRequest.getPage(), pageRequest.getSize());
    }

    @Transactional
    public void removeKnowledge(Long knowledgeId) {
        Knowledge knowledge = knowledgeRepository.findById(knowledgeId)
                .orElseThrow(() -> new BusinessException(404, "知识不存在"));
        knowledge.setStatus(KnowledgeStatus.DELETED);
        knowledgeRepository.save(knowledge);
        log.info("Knowledge {} marked as DELETED", knowledgeId);
    }

    // ─── Access Stats ─────────────────────────────────────────────

    /**
     * Aggregate access counts grouped by accessType for the last 7 days.
     */
    public Map<String, Long> getAccessStats() {
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        List<Object[]> results = entityManager.createQuery(
                "SELECT a.accessType, COUNT(a) FROM AccessLog a WHERE a.createdAt >= :since GROUP BY a.accessType",
                Object[].class
        ).setParameter("since", since).getResultList();

        return results.stream()
                .collect(Collectors.toMap(
                        row -> ((com.kv.common.constant.AccessType) row[0]).getCode(),
                        row -> (Long) row[1],
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }

    // ─── Blacklist ────────────────────────────────────────────────

    public PageResponse<Blacklist> getBlacklist(PageRequest pageRequest) {
        Pageable pageable = toPageable(pageRequest);
        Page<Blacklist> page = blacklistRepository.findAll(pageable);
        return new PageResponse<>(page.getContent(), page.getTotalElements(),
                pageRequest.getPage(), pageRequest.getSize());
    }

    // ─── Private Helpers ──────────────────────────────────────────

    /**
     * Convert our PageRequest to Spring's Pageable, with optional sort parsing.
     * Sort format: "field,direction" (e.g. "createdAt,desc").
     */
    private Pageable toPageable(PageRequest pageRequest) {
        int page = Math.max(pageRequest.getPage() - 1, 0);
        int size = pageRequest.getSize();
        Sort sort = parseSort(pageRequest.getSort());
        return org.springframework.data.domain.PageRequest.of(page, size, sort);
    }

    private Sort parseSort(String sortStr) {
        if (sortStr == null || sortStr.isBlank()) {
            return Sort.unsorted();
        }
        String[] parts = sortStr.split(",");
        if (parts.length >= 2) {
            Sort.Direction direction = "desc".equalsIgnoreCase(parts[1].trim())
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            return Sort.by(direction, parts[0].trim());
        }
        return Sort.by(Sort.Direction.DESC, parts[0].trim());
    }
}
