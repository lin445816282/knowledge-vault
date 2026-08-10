package com.kv.core.service;

import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.common.exception.BusinessException;
import com.kv.common.util.AesUtil;
import com.kv.core.entity.Knowledge;
import com.kv.core.entity.KnowledgeVersion;
import com.kv.core.repository.KnowledgeRepository;
import com.kv.core.repository.KnowledgeVersionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeVersionService {

    private final KnowledgeVersionRepository knowledgeVersionRepository;
    private final KnowledgeRepository knowledgeRepository;

    // ──────────────────────────────────────────────
    // AES 辅助方法
    // ──────────────────────────────────────────────

    private String decryptContent(String encryptedContent) {
        if (encryptedContent == null || !encryptedContent.contains(":")) return encryptedContent;
        String[] parts = encryptedContent.split(":", 2);
        return AesUtil.decrypt(parts[1], parts[0]);
    }

    // ──────────────────────────────────────────────
    // 旧 CRUD 骨架（保持兼容）
    // ──────────────────────────────────────────────

    public List<KnowledgeVersion> list(Long knowledgeId, PageRequest request) {
        return knowledgeVersionRepository.findByKnowledgeIdOrderByVersionNumDesc(knowledgeId);
    }

    public KnowledgeVersion getById(Long knowledgeId, Long versionId) {
        KnowledgeVersion version = knowledgeVersionRepository.findById(versionId)
                .orElseThrow(() -> new BusinessException(404, "版本不存在"));
        if (!version.getKnowledgeId().equals(knowledgeId)) {
            throw new BusinessException(400, "版本不属于该知识");
        }
        version.setContentEncrypted(decryptContent(version.getContentEncrypted()));
        return version;
    }

    @Transactional
    public KnowledgeVersion create(Long knowledgeId, KnowledgeVersion entity) {
        Knowledge knowledge = knowledgeRepository.findById(knowledgeId)
                .orElseThrow(() -> new BusinessException(404, "知识不存在"));
        knowledge.setVersionNum(knowledge.getVersionNum() + 1);

        KnowledgeVersion version = new KnowledgeVersion();
        version.setKnowledgeId(knowledgeId);
        version.setVersionNum(knowledge.getVersionNum());
        version.setContentEncrypted(knowledge.getContentEncrypted());
        version.setChangeLog(entity.getChangeLog());
        version.setEditorId(entity.getEditorId());

        knowledgeRepository.save(knowledge);
        KnowledgeVersion saved = knowledgeVersionRepository.save(version);
        log.info("版本创建成功: knowledgeId={}, versionNum={}", knowledgeId, saved.getVersionNum());
        return saved;
    }

    @Transactional
    public KnowledgeVersion update(Long knowledgeId, Long versionId, KnowledgeVersion entity) {
        KnowledgeVersion version = knowledgeVersionRepository.findById(versionId)
                .orElseThrow(() -> new BusinessException(404, "版本不存在"));
        if (!version.getKnowledgeId().equals(knowledgeId)) {
            throw new BusinessException(400, "版本不属于该知识");
        }
        if (entity.getChangeLog() != null) {
            version.setChangeLog(entity.getChangeLog());
        }
        return knowledgeVersionRepository.save(version);
    }

    @Transactional
    public void delete(Long knowledgeId, Long versionId) {
        KnowledgeVersion version = knowledgeVersionRepository.findById(versionId)
                .orElseThrow(() -> new BusinessException(404, "版本不存在"));
        if (!version.getKnowledgeId().equals(knowledgeId)) {
            throw new BusinessException(400, "版本不属于该知识");
        }
        knowledgeVersionRepository.delete(version);
        log.info("版本已删除: knowledgeId={}, versionId={}", knowledgeId, versionId);
    }

    // ──────────────────────────────────────────────
    // M5 新增：版本管理核心方法
    // ──────────────────────────────────────────────

    /**
     * 为知识创建一个新版本快照。
     * 自动递增知识的 versionNum，保存当前内容为版本记录。
     */
    @Transactional
    public KnowledgeVersion createVersion(Long knowledgeId, Long editorId, String changeLog) {
        Knowledge knowledge = knowledgeRepository.findById(knowledgeId)
                .orElseThrow(() -> new BusinessException(404, "知识不存在"));

        knowledge.setVersionNum(knowledge.getVersionNum() + 1);

        KnowledgeVersion version = new KnowledgeVersion();
        version.setKnowledgeId(knowledgeId);
        version.setVersionNum(knowledge.getVersionNum());
        version.setContentEncrypted(knowledge.getContentEncrypted());
        version.setChangeLog(changeLog);
        version.setEditorId(editorId);

        knowledgeRepository.save(knowledge);
        KnowledgeVersion saved = knowledgeVersionRepository.save(version);
        log.info("版本快照创建成功: knowledgeId={}, versionNum={}, editorId={}",
                knowledgeId, saved.getVersionNum(), editorId);
        return saved;
    }

    /**
     * 列出知识的所有版本（按版本号倒序）。
     */
    public List<KnowledgeVersion> listVersions(Long knowledgeId) {
        return knowledgeVersionRepository.findByKnowledgeIdOrderByVersionNumDesc(knowledgeId);
    }

    /**
     * 获取单个版本详情（内容解密后返回）。
     */
    public KnowledgeVersion getVersion(Long knowledgeId, Long versionId) {
        KnowledgeVersion version = knowledgeVersionRepository.findById(versionId)
                .orElseThrow(() -> new BusinessException(404, "版本不存在"));
        if (!version.getKnowledgeId().equals(knowledgeId)) {
            throw new BusinessException(400, "版本不属于该知识");
        }
        version.setContentEncrypted(decryptContent(version.getContentEncrypted()));
        return version;
    }

    /**
     * 回滚到指定版本。
     * 先备份当前状态为一个新版本，再恢复目标版本的内容。
     */
    @Transactional
    public Knowledge rollback(Long knowledgeId, Long versionId, Long editorId) {
        KnowledgeVersion targetVersion = knowledgeVersionRepository.findById(versionId)
                .orElseThrow(() -> new BusinessException(404, "版本不存在"));
        if (!targetVersion.getKnowledgeId().equals(knowledgeId)) {
            throw new BusinessException(400, "版本不属于该知识");
        }

        Knowledge knowledge = knowledgeRepository.findById(knowledgeId)
                .orElseThrow(() -> new BusinessException(404, "知识不存在"));

        // 先创建当前状态的备份版本
        createVersion(knowledgeId, editorId, "回滚前自动备份（回滚到版本 v" + versionId + "）");

        // 恢复目标版本的内容
        knowledge.setContentEncrypted(targetVersion.getContentEncrypted());
        knowledge.setVersionNum(knowledge.getVersionNum() + 1);
        Knowledge updated = knowledgeRepository.save(knowledge);

        log.info("知识回滚成功: knowledgeId={}, 目标版本={}, editorId={}",
                knowledgeId, versionId, editorId);
        return updated;
    }

    /**
     * 对比两个版本的内容差异。
     * 返回两个版本的明文内容及元数据，供前端展示 diff。
     */
    public Map<String, Object> diff(Long knowledgeId, Long v1Id, Long v2Id) {
        KnowledgeVersion v1 = knowledgeVersionRepository.findById(v1Id)
                .orElseThrow(() -> new BusinessException(404, "版本 v1 不存在"));
        KnowledgeVersion v2 = knowledgeVersionRepository.findById(v2Id)
                .orElseThrow(() -> new BusinessException(404, "版本 v2 不存在"));

        if (!v1.getKnowledgeId().equals(knowledgeId) || !v2.getKnowledgeId().equals(knowledgeId)) {
            throw new BusinessException(400, "版本不属于该知识");
        }

        Map<String, Object> result = new LinkedHashMap<>();

        Map<String, Object> v1Map = new LinkedHashMap<>();
        v1Map.put("versionId", v1.getId());
        v1Map.put("versionNum", v1.getVersionNum());
        v1Map.put("changeLog", v1.getChangeLog());
        v1Map.put("editorId", v1.getEditorId());
        v1Map.put("createdAt", v1.getCreatedAt());
        v1Map.put("content", decryptContent(v1.getContentEncrypted()));
        result.put("v1", v1Map);

        Map<String, Object> v2Map = new LinkedHashMap<>();
        v2Map.put("versionId", v2.getId());
        v2Map.put("versionNum", v2.getVersionNum());
        v2Map.put("changeLog", v2.getChangeLog());
        v2Map.put("editorId", v2.getEditorId());
        v2Map.put("createdAt", v2.getCreatedAt());
        v2Map.put("content", decryptContent(v2.getContentEncrypted()));
        result.put("v2", v2Map);

        return result;
    }
}
