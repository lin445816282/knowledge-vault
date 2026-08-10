package com.kv.core.entity;

import com.kv.common.constant.AccessType;
import com.kv.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "access_logs", indexes = {
    @Index(name = "idx_access_time", columnList = "createdAt"),
    @Index(name = "idx_access_type", columnList = "accessType"),
    @Index(name = "idx_access_ip", columnList = "ip")
})
public class AccessLog extends BaseEntity {

    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccessType accessType;

    @Column(length = 50)
    private String ip;

    @Column(length = 64)
    private String deviceFingerprint;

    @Column(length = 500)
    private String userAgent;

    @Column(length = 500)
    private String requestUri;

    @Column(length = 10)
    private String method;

    /** 是否被风控拦截 */
    private Boolean blocked = false;

    /** 拦截原因 */
    @Column(length = 200)
    private String blockReason;

    /** 策略快照 JSON */
    @Column(columnDefinition = "TEXT")
    private String strategySnapshot;

    private Integer statusCode;
}
