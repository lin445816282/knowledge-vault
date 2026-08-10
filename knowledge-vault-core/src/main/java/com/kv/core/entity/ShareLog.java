package com.kv.core.entity;

import com.kv.common.constant.AccessType;
import com.kv.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "share_logs")
public class ShareLog extends BaseEntity {

    @Column(nullable = false)
    private Long knowledgeId;

    @Column(nullable = false)
    private Long sharerId;

    /** 分享短Token */
    @Column(nullable = false, unique = true, length = 64)
    private String shareToken;

    /** 过期时间 */
    private LocalDateTime expireAt;

    /** 设备限制（逗号分隔设备指纹，空=不限） */
    @Column(length = 500)
    private String deviceLimit;

    /** 访问者ID（null=未登录） */
    private Long visitorId;

    /** 访问类型 */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AccessType accessType;

    /** 访问时间 */
    private LocalDateTime accessedAt;

    /** 是否已撤销 */
    private Boolean revoked = false;
}
