package com.kv.core.entity;

import com.kv.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "balance_logs")
public class BalanceLog extends BaseEntity {

    @Column(nullable = false)
    private Long userId;

    /** RECHARGE / CONSUME / REFUND / EARN */
    @Column(nullable = false, length = 20)
    private String type;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal balanceBefore;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal balanceAfter;

    private Long orderId;

    @Column(length = 200)
    private String remark;
}
