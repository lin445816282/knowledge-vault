package com.kv.core.service;

import com.kv.common.constant.AccessType;
import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.core.entity.AccessLog;
import com.kv.core.repository.AccessLogRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessLogService {

    private final AccessLogRepository accessLogRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Synchronous access logging. Extracts request metadata, builds an AccessLog
     * entity, and persists it within a transaction.
     */
    @Transactional
    public void log(HttpServletRequest request, AccessType type, boolean blocked, String blockReason) {
        AccessLog accessLog = buildAccessLog(request, type, blocked, blockReason);
        accessLogRepository.save(accessLog);
        log.info("Access logged: type={}, ip={}, uri={} {}, blocked={}, reason={}",
                type.getCode(), accessLog.getIp(), accessLog.getMethod(),
                accessLog.getRequestUri(), blocked, blockReason);
    }

    /**
     * Async variant of {@link #log}. Requires {@code @EnableAsync} on the
     * application configuration class. Runs in a separate thread pool so the
     * caller is not blocked by the persistence write.
     */
    @Async
    @Transactional
    public void logAsync(HttpServletRequest request, AccessType type, boolean blocked, String blockReason) {
        log(request, type, blocked, blockReason);
    }

    /**
     * Paginated query of recent access logs, ordered by creation time descending.
     *
     * @param pageRequest pagination parameters (page is 1-based)
     * @return page of AccessLog entities
     */
    public PageResponse<AccessLog> getRecentLogs(PageRequest pageRequest) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                pageRequest.getPage() - 1,
                pageRequest.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<AccessLog> page = accessLogRepository.findAll(pageable);
        return new PageResponse<>(
                page.getContent(),
                page.getTotalElements(),
                pageRequest.getPage(),
                pageRequest.getSize()
        );
    }

    /**
     * Aggregate access counts grouped by {@link AccessType} for the last 24 hours.
     *
     * @return map of AccessType → count (insertion-ordered)
     */
    public Map<AccessType, Long> getStats() {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        TypedQuery<Object[]> query = entityManager.createQuery(
                "SELECT a.accessType, COUNT(a) FROM AccessLog a WHERE a.createdAt >= :since GROUP BY a.accessType",
                Object[].class
        );
        query.setParameter("since", since);
        List<Object[]> results = query.getResultList();
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (AccessType) row[0],
                        row -> (Long) row[1],
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }

    // ─── private helpers ────────────────────────────────────────────

    private AccessLog buildAccessLog(HttpServletRequest request, AccessType type,
                                     boolean blocked, String blockReason) {
        AccessLog accessLog = new AccessLog();
        accessLog.setIp(extractClientIp(request));
        accessLog.setUserAgent(request.getHeader("User-Agent"));
        accessLog.setRequestUri(request.getRequestURI());
        accessLog.setMethod(request.getMethod());
        accessLog.setAccessType(type);
        accessLog.setBlocked(blocked);
        accessLog.setBlockReason(blockReason);
        accessLog.setStatusCode(blocked ? 403 : 200);
        return accessLog;
    }

    /**
     * Extracts the real client IP, checking common proxy headers first.
     */
    private String extractClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
