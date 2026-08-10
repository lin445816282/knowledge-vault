package com.kv.core.service;

import com.kv.common.constant.KnowledgeStatus;
import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.common.exception.BusinessException;
import com.kv.common.util.AesUtil;
import com.kv.core.entity.Knowledge;
import com.kv.core.repository.KnowledgeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeService {

    private final KnowledgeRepository knowledgeRepository;

    // ──────────────────────────────────────────────
    // 辅助方法
    // ──────────────────────────────────────────────

    private Pageable toPageable(PageRequest request) {
        int page = Math.max(request.getPage() - 1, 0);
        int size = Math.max(request.getSize(), 1);
        return org.springframework.data.domain.PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private <T> PageResponse<T> toPageResponse(Page<T> page) {
        return new PageResponse<>(page.getContent(), page.getTotalElements(), page.getNumber() + 1, page.getSize());
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        throw new BusinessException(401, "未登录");
    }

    // ──────────────────────────────────────────────
    // CRUD
    // ──────────────────────────────────────────────

    /**
     * 根据 ID 获取知识（含权限校验）。
     * 私有知识仅允许作者本人查看。
     */
    public Knowledge getById(Long id) {
        Knowledge knowledge = knowledgeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "知识不存在"));

        if (knowledge.getStatus() == KnowledgeStatus.PRIVATE) {
            Long currentUserId = getCurrentUserId();
            if (!currentUserId.equals(knowledge.getUserId())) {
                throw new BusinessException(403, "无权访问");
            }
        }

        knowledge.setContentEncrypted(decryptContent(knowledge.getContentEncrypted()));

        return knowledge;
    }

    /**
     * 创建知识。自动设置作者、状态和版本号。
     */
    @Transactional
    public Knowledge create(Knowledge entity) {
        Long userId = getCurrentUserId();
        entity.setUserId(userId);
        entity.setStatus(KnowledgeStatus.PRIVATE);
        entity.setVersionNum(1);
        if (entity.getViewCount() == null) {
            entity.setViewCount(0L);
        }
        if (entity.getCollectCount() == null) {
            entity.setCollectCount(0L);
        }

        entity.setContentEncrypted(encryptContent(entity.getContentEncrypted()));

        Knowledge saved = knowledgeRepository.save(entity);
        log.info("知识创建成功: id={}, userId={}, title={}", saved.getId(), userId, saved.getTitle());
        return saved;
    }

    /**
     * 更新知识。仅允许作者本人编辑。
     */
    @Transactional
    public Knowledge update(Long id, Knowledge dto) {
        Knowledge knowledge = getById(id);

        // 校验所有权
        Long currentUserId = getCurrentUserId();
        if (!currentUserId.equals(knowledge.getUserId())) {
            throw new BusinessException(403, "无权编辑此知识");
        }

        // 更新允许修改的字段
        if (dto.getTitle() != null) {
            knowledge.setTitle(dto.getTitle());
        }
        if (dto.getSummary() != null) {
            knowledge.setSummary(dto.getSummary());
        }
        if (dto.getDifficulty() != null) {
            knowledge.setDifficulty(dto.getDifficulty());
        }
        if (dto.getTags() != null) {
            knowledge.setTags(dto.getTags());
        }
        if (dto.getStatus() != null) {
            knowledge.setStatus(dto.getStatus());
        }
        if (dto.getPrice() != null) {
            knowledge.setPrice(dto.getPrice());
        }
        if (dto.getCategoryId() != null) {
            knowledge.setCategoryId(dto.getCategoryId());
        }
        if (dto.getContentEncrypted() != null) {
            knowledge.setContentEncrypted(dto.getContentEncrypted());
        }
        if (dto.getEncryptionKey() != null) {
            knowledge.setEncryptionKey(dto.getEncryptionKey());
        }
        if (dto.getTargetAudience() != null) {
            knowledge.setTargetAudience(dto.getTargetAudience());
        }
        if (dto.getNoCopy() != null) {
            knowledge.setNoCopy(dto.getNoCopy());
        }
        if (dto.getNoSelect() != null) {
            knowledge.setNoSelect(dto.getNoSelect());
        }
        if (dto.getAllowAiCrawl() != null) {
            knowledge.setAllowAiCrawl(dto.getAllowAiCrawl());
        }

        Knowledge updated = knowledgeRepository.save(knowledge);
        log.info("知识更新成功: id={}", id);
        return updated;
    }

    /**
     * 删除知识（软删除，状态置为 DELETED）。仅允许作者本人操作。
     */
    @Transactional
    public void delete(Long id) {
        Knowledge knowledge = getById(id);

        Long currentUserId = getCurrentUserId();
        if (!currentUserId.equals(knowledge.getUserId())) {
            throw new BusinessException(403, "无权删除此知识");
        }

        knowledge.setStatus(KnowledgeStatus.DELETED);
        knowledgeRepository.save(knowledge);
        log.info("知识已删除: id={}", id);
    }

    /**
     * 分页列出所有知识（管理员使用）。
     */
    public PageResponse<Knowledge> list(PageRequest request) {
        Page<Knowledge> page = knowledgeRepository.findAll(toPageable(request));
        return toPageResponse(page);
    }

    // ──────────────────────────────────────────────
    // 新增方法
    // ──────────────────────────────────────────────

    /**
     * 分页列出所有公开知识（无需登录）。
     */
    public PageResponse<Knowledge> listPublic(PageRequest request) {
        Page<Knowledge> page = knowledgeRepository.findByStatus(KnowledgeStatus.PUBLIC, toPageable(request));
        return toPageResponse(page);
    }

    /**
     * 分页列出当前用户的知识。
     */
    public PageResponse<Knowledge> listMyKnowledge(Long userId, PageRequest request) {
        Page<Knowledge> page = knowledgeRepository.findByUserId(userId, toPageable(request));
        return toPageResponse(page);
    }

    /**
     * 根据关键词搜索知识（匹配标题或标签）。
     */
    public PageResponse<Knowledge> search(String keyword, PageRequest request) {
        Page<Knowledge> page = knowledgeRepository.findByTitleContainingOrTagsContaining(
                keyword, keyword, toPageable(request));
        return toPageResponse(page);
    }

    /**
     * 增加浏览次数。
     */
    @Transactional
    public void incrementViewCount(Long id) {
        Knowledge knowledge = knowledgeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "知识不存在"));
        knowledge.setViewCount(knowledge.getViewCount() + 1);
        knowledgeRepository.save(knowledge);
    }

    // ──────────────────────────────────────────────
    // AES 加密/解密
    // ──────────────────────────────────────────────

    private String encryptContent(String plainContent) {
        String aesKey = AesUtil.generateKey();
        return aesKey + ":" + AesUtil.encrypt(plainContent, aesKey);
    }

    private String decryptContent(String encryptedContent) {
        if (encryptedContent == null || !encryptedContent.contains(":")) return encryptedContent;
        String[] parts = encryptedContent.split(":", 2);
        return AesUtil.decrypt(parts[1], parts[0]);
    }
}
