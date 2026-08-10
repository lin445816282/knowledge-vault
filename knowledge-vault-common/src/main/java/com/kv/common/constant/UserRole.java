package com.kv.common.constant;

import lombok.Getter;

/**
 * 用户角色：普通用户 / VIP / 创作者 / 管理员
 */
@Getter
public enum UserRole {
    USER("ROLE_USER", "普通用户"),
    VIP("ROLE_VIP", "VIP会员"),
    CREATOR("ROLE_CREATOR", "创作者"),
    ADMIN("ROLE_ADMIN", "管理员");

    private final String roleCode;
    private final String description;

    UserRole(String roleCode, String description) {
        this.roleCode = roleCode;
        this.description = description;
    }
}
