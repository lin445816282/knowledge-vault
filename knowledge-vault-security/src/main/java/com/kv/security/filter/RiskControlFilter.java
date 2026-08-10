package com.kv.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 全局风控过滤器 — 最高优先级，所有请求先过风控
 * Phase 1 骨架：先放行所有请求，后续填实
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RiskControlFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // TODO Phase 8: UA黑名单检测、访问分类、IP限流、设备指纹
        // TODO Phase 8: 拦截逻辑：blocked=true → 返回403 + 记录日志

        chain.doFilter(request, response);
    }
}
