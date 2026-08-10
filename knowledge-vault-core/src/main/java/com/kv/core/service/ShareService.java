package com.kv.core.service;

import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.common.exception.BusinessException;
import com.kv.core.entity.Knowledge;
import com.kv.core.entity.ShareLog;
import com.kv.core.repository.KnowledgeRepository;
import com.kv.core.repository.ShareLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShareService {

    private final ShareLogRepository shareLogRepository;
    private final KnowledgeRepository knowledgeRepository;

    // ──────────────────────────────────────────────
    // 分享操作
    // ──────────────────────────────────────────────

    /**
     * 创建分享链接。
     *
     * @param knowledgeId 知识ID
     * @param sharerId    分享者用户ID
     * @param expireHours 过期小时数（0或负数表示永不过期）
     * @param deviceLimit 设备限制（逗号分隔设备指纹，空=不限）
     * @return 分享令牌
     */
    @Transactional
    public String share(Long knowledgeId, Long sharerId, int expireHours, String deviceLimit) {
        // 验证知识存在且属于当前用户
        Knowledge knowledge = knowledgeRepository.findById(knowledgeId)
                .orElseThrow(() -> new BusinessException(404, "知识不存在"));

        if (!knowledge.getUserId().equals(sharerId)) {
            throw new BusinessException(403, "无权分享该知识");
        }

        // 生成分享令牌
        String shareToken = UUID.randomUUID().toString().replace("-", "");

        // 计算过期时间
        LocalDateTime expireAt = null;
        if (expireHours > 0) {
            expireAt = LocalDateTime.now().plusHours(expireHours);
        }

        // 创建分享记录
        ShareLog shareLog = new ShareLog();
        shareLog.setKnowledgeId(knowledgeId);
        shareLog.setSharerId(sharerId);
        shareLog.setShareToken(shareToken);
        shareLog.setExpireAt(expireAt);
        shareLog.setDeviceLimit(deviceLimit);
        shareLog.setRevoked(false);

        shareLogRepository.save(shareLog);
        log.info("分享创建成功: knowledgeId={}, sharerId={}, token={}", knowledgeId, sharerId, shareToken);

        return shareToken;
    }

    /**
     * 通过令牌访问分享的知识。
     *
     * @param token 分享令牌
     * @return 知识实体（公开字段）
     */
    public Knowledge access(String token) {
        ShareLog shareLog = shareLogRepository.findByShareToken(token)
                .orElseThrow(() -> new BusinessException(404, "分享不存在或已失效"));

        if (Boolean.TRUE.equals(shareLog.getRevoked())) {
            throw new BusinessException(404, "分享不存在或已失效");
        }

        if (shareLog.getExpireAt() != null && shareLog.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(410, "分享已过期");
        }

        Knowledge knowledge = knowledgeRepository.findById(shareLog.getKnowledgeId())
                .orElseThrow(() -> new BusinessException(404, "知识不存在"));

        // 记录访问
        shareLog.setAccessedAt(LocalDateTime.now());
        shareLog.setAccessType(com.kv.common.constant.AccessType.HUMAN);
        shareLogRepository.save(shareLog);

        log.info("分享访问: token={}, knowledgeId={}", token, shareLog.getKnowledgeId());

        // 返回知识（公开字段，不清除敏感字段，由调用方控制）
        return knowledge;
    }

    /**
     * 撤销分享。
     *
     * @param token  分享令牌
     * @param userId 操作用户ID
     */
    @Transactional
    public void revoke(String token, Long userId) {
        ShareLog shareLog = shareLogRepository.findByShareToken(token)
                .orElseThrow(() -> new BusinessException(404, "分享不存在"));

        if (!shareLog.getSharerId().equals(userId)) {
            throw new BusinessException(403, "无权撤销该分享");
        }

        shareLog.setRevoked(true);
        shareLogRepository.save(shareLog);
        log.info("分享已撤销: token={}, userId={}", token, userId);
    }

    // ──────────────────────────────────────────────
    // 查询操作
    // ──────────────────────────────────────────────

    /**
     * 查询我的分享列表（分页）。
     */
    public PageResponse<ShareLog> listMyShares(Long userId, PageRequest request) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                Math.max(0, request.getPage() - 1),
                Math.max(1, request.getSize())
        );
        Page<ShareLog> page = shareLogRepository.findBySharerId(userId, pageable);
        return new PageResponse<>(page.getContent(), page.getTotalElements(), request.getPage(), request.getSize());
    }

    /**
     * 查询某个分享的访问记录列表。
     * 通过 token 找到 knowledgeId，然后查询该知识的所有访问记录。
     */
    public List<ShareLog> listAccessLogs(String token, Long userId) {
        ShareLog shareLog = shareLogRepository.findByShareToken(token)
                .orElseThrow(() -> new BusinessException(404, "分享不存在"));

        // 验证查询者是否为分享者本人
        if (!shareLog.getSharerId().equals(userId)) {
            throw new BusinessException(403, "无权查看访问记录");
        }

        return shareLogRepository.findByKnowledgeIdAndAccessedAtIsNotNull(shareLog.getKnowledgeId());
    }

    // ──────────────────────────────────────────────
    // 原有骨架方法（保留兼容）
    // ──────────────────────────────────────────────

    public List<ShareLog> list(PageRequest request) {
        log.warn("ShareService.list() 为骨架方法，请使用 listMyShares()");
        return shareLogRepository.findAll();
    }

    public ShareLog getById(Long id) {
        return shareLogRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "分享记录不存在"));
    }

    @Transactional
    public ShareLog create(ShareLog entity) {
        return shareLogRepository.save(entity);
    }

    @Transactional
    public ShareLog update(Long id, ShareLog entity) {
        ShareLog existing = shareLogRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "分享记录不存在"));
        existing.setDeviceLimit(entity.getDeviceLimit());
        existing.setRevoked(entity.getRevoked());
        existing.setExpireAt(entity.getExpireAt());
        return shareLogRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!shareLogRepository.existsById(id)) {
            throw new BusinessException(404, "分享记录不存在");
        }
        shareLogRepository.deleteById(id);
    }
}
