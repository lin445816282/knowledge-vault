package com.kv.core.entity;

import com.kv.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "blacklist")
public class Blacklist extends BaseEntity {

    /** IP / DEVICE / USER */
    @Column(nullable = false, length = 20)
    private String targetType;

    /** IP地址 / 设备指纹 / 用户ID */
    @Column(nullable = false, length = 200)
    private String targetValue;

    @Column(length = 500)
    private String reason;

    private LocalDateTime blockedAt;

    /** 过期时间，null=永久 */
    private LocalDateTime expireAt;

    @Column(nullable = false)
    private Boolean permanent = false;
}
