package com.kv.security.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 简易 IP 限流服务 — 基于滑动窗口计数器
 *
 * 生产环境可替换为 Redis + Lua 脚本实现分布式限流。
 */
@Slf4j
@Service
public class RateLimitService {

    /** 默认窗口：60 秒 */
    private static final long DEFAULT_WINDOW_MS = 60_000L;

    /** 默认每窗口最大请求数 */
    private static final int DEFAULT_MAX_REQUESTS = 300;

    /** IP → 窗口起始时间戳 */
    private final Map<String, AtomicLong> windowStartMap = new ConcurrentHashMap<>();

    /** IP → 当前窗口计数 */
    private final Map<String, AtomicInteger> counterMap = new ConcurrentHashMap<>();

    private final long windowMs;
    private final int maxRequests;

    public RateLimitService() {
        this(DEFAULT_WINDOW_MS, DEFAULT_MAX_REQUESTS);
    }

    public RateLimitService(long windowMs, int maxRequests) {
        this.windowMs = windowMs;
        this.maxRequests = maxRequests;
    }

    /**
     * 检查 IP 是否允许访问
     * @param ip 客户端 IP
     * @return true=允许, false=限流拒绝
     */
    public boolean isAllowed(String ip) {
        if (ip == null || ip.isBlank()) {
            return false;
        }

        long now = System.currentTimeMillis();
        AtomicLong windowStart = windowStartMap.computeIfAbsent(ip, k -> new AtomicLong(now));
        AtomicInteger counter = counterMap.computeIfAbsent(ip, k -> new AtomicInteger(0));

        synchronized (counter) {
            long ws = windowStart.get();

            // 窗口过期 → 重置
            if (now - ws > windowMs) {
                windowStart.set(now);
                counter.set(1);
                return true;
            }

            int count = counter.incrementAndGet();
            if (count <= maxRequests) {
                return true;
            }

            log.warn("Rate limit exceeded for IP: {}, count: {}/{}", ip, count, maxRequests);
            return false;
        }
    }

    /**
     * 获取当前窗口剩余配额（用于监控/调试）
     */
    public int remainingQuota(String ip) {
        AtomicInteger counter = counterMap.get(ip);
        if (counter == null) {
            return maxRequests;
        }
        return Math.max(0, maxRequests - counter.get());
    }
}
