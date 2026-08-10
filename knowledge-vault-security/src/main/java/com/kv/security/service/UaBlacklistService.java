package com.kv.security.service;

import com.kv.common.constant.AccessType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * UA 黑名单检测服务 — 基于 User-Agent 字符串分类访问类型
 */
@Slf4j
@Service
public class UaBlacklistService {

    // AI 训练爬虫特征
    private static final Set<String> AI_BOT_PATTERNS = Set.of(
            "GPTBot", "ChatGPT-User", "CCBot", "anthropic-ai",
            "Claude-Web", "ClaudeBot", "cohere-ai", "PerplexityBot",
            "Google-Extended", "GoogleOther", "Amazonbot", "Applebot-Extended",
            "Bytespider", "Diffbot", "FacebookBot", "ImagesiftBot",
            "Omgilibot", "Omgili", "peer39_crawler", "YouBot",
            "AI2Bot", "Ai2Bot-Dolma", "cohere-training-data-crawler",
            "omgili", "omgilibot", "ttrpg-bot"
    );

    // 脚本/无头浏览器特征
    private static final Set<String> SCRIPT_BOT_PATTERNS = Set.of(
            "HeadlessChrome", "PhantomJS", "Puppeteer", "Playwright",
            "Selenium", "Cypress", "WebDriver", "scrapy", "python-requests",
            "Go-http-client", "curl", "Wget", "libwww-perl", "Java/",
            "Apache-HttpClient", "okhttp", "axios"
    );

    // 普通搜索引擎爬虫
    private static final Set<String> GENERAL_BOT_PATTERNS = Set.of(
            "Googlebot", "Bingbot", "Slurp", "DuckDuckBot", "Baiduspider",
            "YandexBot", "Sogou", "Exabot", "facebot", "ia_archiver",
            "Twitterbot", "LinkedInBot", "Pinterestbot", "Discordbot",
            "TelegramBot", "WhatsApp", "Slackbot", "AhrefsBot",
            "SemrushBot", "DotBot", "MJ12bot", "Rogerbot"
    );

    // 攻击扫描特征
    private static final Set<String> ATTACK_PATTERNS = Set.of(
            "Nmap", "Nikto", "sqlmap", "Burp Suite", "ZAP",
            "acunetix", "nessus", "masscan", "zgrab", "gobuster",
            "dirbuster", "wfuzz", "ffuf", "nuclei"
    );

    // 额外正则：检测常见的无 UA 或异常短 UA
    private static final Pattern EMPTY_OR_INVALID_UA = Pattern.compile(
            "^$|^-$|^unknown$|^null$|^\\s*$",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 分类 User-Agent，返回访问类型
     */
    public AccessType classify(String userAgent) {
        if (userAgent == null || EMPTY_OR_INVALID_UA.matcher(userAgent).matches()) {
            log.debug("UA classify: empty/invalid UA → UNKNOWN");
            return AccessType.UNKNOWN;
        }

        String uaLower = userAgent.toLowerCase();

        // 1. 攻击扫描（最高优先级）
        for (String pattern : ATTACK_PATTERNS) {
            if (uaLower.contains(pattern.toLowerCase())) {
                log.info("UA classify: ATTACK pattern matched '{}' in UA '{}'", pattern, userAgent);
                return AccessType.BOT_ATTACK;
            }
        }

        // 2. AI 训练爬虫
        for (String pattern : AI_BOT_PATTERNS) {
            if (uaLower.contains(pattern.toLowerCase())) {
                log.info("UA classify: AI_BOT pattern matched '{}' in UA '{}'", pattern, userAgent);
                return AccessType.BOT_AI;
            }
        }

        // 3. 脚本/无头浏览器
        for (String pattern : SCRIPT_BOT_PATTERNS) {
            if (uaLower.contains(pattern.toLowerCase())) {
                log.info("UA classify: SCRIPT pattern matched '{}' in UA '{}'", pattern, userAgent);
                return AccessType.BOT_SCRIPT;
            }
        }

        // 4. 普通搜索引擎
        for (String pattern : GENERAL_BOT_PATTERNS) {
            if (uaLower.contains(pattern.toLowerCase())) {
                log.debug("UA classify: GENERAL_BOT pattern matched '{}'", pattern);
                return AccessType.BOT_GENERAL;
            }
        }

        // 5. 默认作为真人访问
        return AccessType.HUMAN;
    }
}
