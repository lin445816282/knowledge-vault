package com.kv.core.service;

import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.common.exception.BusinessException;
import com.kv.core.entity.Blacklist;
import com.kv.core.repository.BlacklistRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final BlacklistRepository blacklistRepository;

    /**
     * Block a target.
     *
     * @param targetType    IP / DEVICE / USER
     * @param targetValue   the actual value (IP address, device fingerprint, user ID)
     * @param reason        reason for blocking
     * @param expireMinutes expiration in minutes; <= 0 means permanent
     * @return the saved Blacklist entity
     */
    @Transactional
    public Blacklist block(String targetType, String targetValue, String reason, int expireMinutes) {
        Blacklist blacklist = new Blacklist();
        blacklist.setTargetType(targetType);
        blacklist.setTargetValue(targetValue);
        blacklist.setReason(reason);
        blacklist.setBlockedAt(LocalDateTime.now());

        if (expireMinutes <= 0) {
            blacklist.setPermanent(true);
            blacklist.setExpireAt(null);
        } else {
            blacklist.setPermanent(false);
            blacklist.setExpireAt(LocalDateTime.now().plusMinutes(expireMinutes));
        }

        blacklist = blacklistRepository.save(blacklist);
        log.info("Blocked {}:{} — reason={}, permanent={}, expireAt={}",
                targetType, targetValue, reason, blacklist.getPermanent(), blacklist.getExpireAt());
        return blacklist;
    }

    /**
     * Remove a block entry so the target is no longer blocked.
     */
    @Transactional
    public void unblock(String targetType, String targetValue) {
        blacklistRepository.findByTargetTypeAndTargetValue(targetType, targetValue)
                .ifPresentOrElse(
                        entry -> {
                            blacklistRepository.delete(entry);
                            log.info("Unblocked {}:{}", targetType, targetValue);
                        },
                        () -> {
                            throw new BusinessException(404,
                                    "No block entry found for " + targetType + ":" + targetValue);
                        }
                );
    }

    /**
     * Check whether a target value is currently blocked.
     * A target is blocked if any matching entry is permanent OR has not yet expired.
     */
    public boolean isBlocked(String targetValue) {
        List<Blacklist> entries = blacklistRepository.findByTargetValue(targetValue);
        LocalDateTime now = LocalDateTime.now();
        return entries.stream().anyMatch(entry ->
                Boolean.TRUE.equals(entry.getPermanent()) ||
                        (entry.getExpireAt() != null && entry.getExpireAt().isAfter(now))
        );
    }

    /**
     * Convenience wrapper: auto-block an IP for 60 minutes with an "[AUTO]" prefixed reason.
     */
    @Transactional
    public Blacklist autoBlock(String ip, String reason) {
        return block("IP", ip, "[AUTO] " + reason, 60);
    }

    /**
     * Paginated listing of all blacklist entries.
     */
    public PageResponse<Blacklist> listAll(PageRequest request) {
        org.springframework.data.domain.PageRequest springPageRequest =
                org.springframework.data.domain.PageRequest.of(
                        request.getPage() - 1,
                        request.getSize()
                );

        Page<Blacklist> page = blacklistRepository.findAll(springPageRequest);
        return new PageResponse<>(
                page.getContent(),
                page.getTotalElements(),
                request.getPage(),
                request.getSize()
        );
    }
}
