package com.kv.security.filter;

import com.kv.common.constant.AccessType;
import com.kv.security.service.RateLimitService;
import com.kv.security.service.UaBlacklistService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 全局风控过滤器 — 最高优先级，所有请求先过风控
 *
 * 流程：
 * 1. 提取真实 IP（含 X-Forwarded-For）
 * 2. IP 黑名单检测（永久或未过期）→ 403
 * 3. IP 限流检测 → 429
 * 4. UA 分类检测（BOT_AI / BOT_SCRIPT）→ 403
 * 5. 设置响应头（反 AI 爬虫声明）
 * 6. 设置请求属性供下游日志使用
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class RiskControlFilter implements Filter {

    private final UaBlacklistService uaBlacklistService;
    private final RateLimitService rateLimitService;

    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String HEADER_X_ROBOTS_TAG = "X-Robots-Tag";
    private static final String HEADER_X_NO_AI = "X-No-AI";
    private static final String ATTR_ACCESS_TYPE = "accessType";
    private static final String ATTR_BLOCKED = "blocked";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // ── 1. 提取真实 IP（优先 X-Forwarded-For，其次 remoteAddr） ──
        String ip = extractClientIp(req);
        String userAgent = req.getHeader("User-Agent");

        log.debug("RiskControl: IP={}, UA={}, URI={}", ip, userAgent, req.getRequestURI());

        // ── 2. IP 黑名单检测（由 core 层 BlacklistInterceptor 处理）──
        // 注：security 模块不依赖 core，避免循环；黑名单检查在 core 拦截器中完成

        // ── 3. IP 限流检测 ──
        if (!rateLimitService.isAllowed(ip)) {
            log.warn("RiskControl: IP {} rate limited — returning 429", ip);
            writeBlockedResponse(res, 429, "Too many requests");
            return;
        }

        // ── 4. UA 分类 ──
        AccessType accessType = uaBlacklistService.classify(userAgent);

        // ── 5. 拦截 AI 爬虫 / 脚本 ──
        if (accessType == AccessType.BOT_AI || accessType == AccessType.BOT_SCRIPT) {
            log.warn("RiskControl: UA classified as {} — returning 403, UA='{}'",
                    accessType.getCode(), userAgent);
            req.setAttribute(ATTR_ACCESS_TYPE, accessType);
            req.setAttribute(ATTR_BLOCKED, true);
            setAntiAiHeaders(res);
            writeBlockedResponse(res, 403,
                    "Access denied: " + accessType.getDescription());
            return;
        }

        // ── 6. 设置反 AI 爬虫响应头 ──
        setAntiAiHeaders(res);

        // ── 7. 设置请求属性供下游（日志、审计）使用 ──
        req.setAttribute(ATTR_ACCESS_TYPE, accessType);
        req.setAttribute(ATTR_BLOCKED, false);

        // ── 8. 放行 ──
        chain.doFilter(request, response);
    }

    /**
     * 提取客户端真实 IP
     */
    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader(HEADER_X_FORWARDED_FOR);
        if (forwarded != null && !forwarded.isBlank()) {
            // X-Forwarded-For 格式: "client, proxy1, proxy2"
            // 取第一个（最原始客户端）
            int commaIdx = forwarded.indexOf(',');
            String clientIp = (commaIdx > 0)
                    ? forwarded.substring(0, commaIdx).trim()
                    : forwarded.trim();
            return clientIp;
        }
        return request.getRemoteAddr();
    }

    /**
     * 设置反 AI 爬虫声明响应头
     */
    private void setAntiAiHeaders(HttpServletResponse response) {
        response.setHeader(HEADER_X_ROBOTS_TAG, "noai, noimageai");
        response.setHeader(HEADER_X_NO_AI, "true");
    }

    /**
     * 写入 JSON 错误响应，不继续 filter chain
     */
    private void writeBlockedResponse(HttpServletResponse response, int status, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        String json = String.format("{\"code\":%d,\"message\":\"%s\"}", status, message);
        response.getWriter().write(json);
    }
}
