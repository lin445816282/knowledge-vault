package com.kv.core.entity;

import com.kv.common.constant.KnowledgeStatus;
import com.kv.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "knowledge")
public class Knowledge extends BaseEntity {

    /** 作者 */
    @Column(nullable = false)
    private Long userId;

    /** 分类 */
    private Long categoryId;

    @Column(nullable = false, length = 200)
    private String title;

    /** AES 加密后的内容 */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String contentEncrypted;

    /** 明文摘要（可展示） */
    @Column(length = 500)
    private String summary;

    @Column(length = 20)
    private String difficulty;

    @Column(length = 200)
    private String targetAudience;

    /** 逗号分隔标签 */
    @Column(length = 500)
    private String tags;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private KnowledgeStatus status = KnowledgeStatus.PRIVATE;

    /** 单篇独立 AES 密钥（RSA 加密存储） */
    @Column(length = 1000)
    private String encryptionKey;

    /** 当前版本号 */
    @Column(nullable = false)
    private Integer versionNum = 1;

    /** 售价（元） */
    @Column(precision = 10, scale = 2)
    private java.math.BigDecimal price;

    /** 浏览次数 */
    private Long viewCount = 0L;

    /** 收藏次数 */
    private Long collectCount = 0L;

    /** 禁止复制 */
    private Boolean noCopy = true;

    /** 禁止选择文本 */
    private Boolean noSelect = true;

    /** 允许 AI 抓取此篇（仅公开时有效） */
    private Boolean allowAiCrawl = false;
}
