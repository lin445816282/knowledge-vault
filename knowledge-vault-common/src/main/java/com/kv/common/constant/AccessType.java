package com.kv.common.constant;

import lombok.Getter;

/**
 * 全行为访问类型 6 分类
 */
@Getter
public enum AccessType {
    HUMAN("human", "真人正常访问"),
    BOT_GENERAL("bot_general", "普通搜索引擎爬虫"),
    BOT_AI("bot_ai", "AI训练爬虫"),
    BOT_SCRIPT("bot_script", "脚本/无头浏览器"),
    BOT_ATTACK("bot_attack", "暴力扫描/ID遍历"),
    UNKNOWN("unknown", "未知访问");

    private final String code;
    private final String description;

    AccessType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
