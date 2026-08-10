package com.kv.common.constant;

import lombok.Getter;

/**
 * 知识状态：私有 / 公开 / 已分享 / 已售卖 / 已删除
 */
@Getter
public enum KnowledgeStatus {
    PRIVATE("PRIVATE", "私有"),
    PUBLIC("PUBLIC", "公开"),
    SHARED("SHARED", "已分享"),
    SOLD("SOLD", "已售卖"),
    DELETED("DELETED", "已删除");

    private final String code;
    private final String description;

    KnowledgeStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
