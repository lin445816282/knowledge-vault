package com.kv.core.service;

import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.common.exception.BusinessException;
import com.kv.core.dto.UserProfileDTO;
import com.kv.core.entity.BalanceLog;
import com.kv.core.entity.User;
import com.kv.core.repository.BalanceLogRepository;
import com.kv.core.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BalanceLogRepository balanceLogRepository;

    public List<User> list(PageRequest request) {
        return null;
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
    }

    @Transactional
    public User create(User entity) {
        return null;
    }

    @Transactional
    public User update(Long id, User entity) {
        return null;
    }

    @Transactional
    public void delete(Long id) {
    }

    /** 获取用户资料 */
    public UserProfileDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        return mapToDTO(user);
    }

    /** 更新用户资料 */
    @Transactional
    public UserProfileDTO updateProfile(Long userId, UserProfileDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        if (dto.getNickname() != null) user.setNickname(dto.getNickname());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getAvatarUrl() != null) user.setAvatarUrl(dto.getAvatarUrl());
        if (dto.getAllowAiCrawl() != null) user.setAllowAiCrawl(dto.getAllowAiCrawl());
        if (dto.getAllowPublicIndex() != null) user.setAllowPublicIndex(dto.getAllowPublicIndex());
        userRepository.save(user);
        return mapToDTO(user);
    }

    /** 修改密码 */
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new BusinessException(400, "原密码错误");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /** 充值 */
    @Transactional
    public BigDecimal topUp(Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        BigDecimal before = user.getBalance();
        user.setBalance(before.add(amount));
        userRepository.save(user);
        // Record balance log
        BalanceLog log = new BalanceLog();
        log.setUserId(userId);
        log.setType("RECHARGE");
        log.setAmount(amount);
        log.setBalanceBefore(before);
        log.setBalanceAfter(user.getBalance());
        log.setRemark("账户充值");
        balanceLogRepository.save(log);
        return user.getBalance();
    }

    private UserProfileDTO mapToDTO(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setNickname(user.getNickname());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setRole(user.getRole().name());
        dto.setBalance(user.getBalance());
        dto.setAllowAiCrawl(user.getAllowAiCrawl());
        dto.setAllowPublicIndex(user.getAllowPublicIndex());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
