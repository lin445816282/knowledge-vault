package com.kv.core.entity;

import com.kv.common.constant.UserRole;
import com.kv.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 50)
    private String nickname;

    @Column(length = 500)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.USER;

    @Column(precision = 12, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    /** AES 加密的个人信息密钥 */
    @Column(length = 500)
    private String personalKey;

    /** 允许 AI 抓取我的公开内容 */
    private Boolean allowAiCrawl = false;

    /** 允许搜索引擎索引 */
    private Boolean allowPublicIndex = true;

    /** 账户是否冻结 */
    private Boolean frozen = false;

    /** 最近登录 IP */
    @Column(length = 50)
    private String lastLoginIp;

    /** 最近登录时间 */
    private java.time.LocalDateTime lastLoginAt;
}
