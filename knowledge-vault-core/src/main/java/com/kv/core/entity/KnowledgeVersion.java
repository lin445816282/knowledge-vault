package com.kv.core.entity;

import com.kv.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "knowledge_versions")
public class KnowledgeVersion extends BaseEntity {

    @Column(nullable = false)
    private Long knowledgeId;

    @Column(nullable = false)
    private Integer versionNum;

    /** 该版本的完整内容（加密） */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String contentEncrypted;

    @Column(length = 500)
    private String changeLog;

    /** 修改人 */
    private Long editorId;
}
