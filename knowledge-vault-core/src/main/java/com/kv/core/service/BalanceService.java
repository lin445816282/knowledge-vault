package com.kv.core.service;

import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.common.exception.BusinessException;
import com.kv.core.entity.BalanceLog;
import com.kv.core.entity.User;
import com.kv.core.repository.BalanceLogRepository;
import com.kv.core.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceLogRepository balanceLogRepository;
    private final UserRepository userRepository;

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在: " + userId));
    }

    @Transactional
    public BigDecimal recharge(Long userId, BigDecimal amount, String remark) {
        User user = getUser(userId);
        BigDecimal balanceBefore = user.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(amount);
        user.setBalance(balanceAfter);
        userRepository.save(user);

        BalanceLog balanceLog = new BalanceLog();
        balanceLog.setUserId(userId);
        balanceLog.setType("RECHARGE");
        balanceLog.setAmount(amount);
        balanceLog.setBalanceBefore(balanceBefore);
        balanceLog.setBalanceAfter(balanceAfter);
        balanceLog.setRemark(remark);
        balanceLogRepository.save(balanceLog);

        log.info("用户 {} 充值 {}，余额 {} -> {}", userId, amount, balanceBefore, balanceAfter);
        return balanceAfter;
    }

    @Transactional
    public BigDecimal consume(Long userId, BigDecimal amount, Long orderId, String remark) {
        User user = getUser(userId);
        BigDecimal balanceBefore = user.getBalance();

        if (balanceBefore.compareTo(amount) < 0) {
            throw new BusinessException(400, "余额不足，当前余额: " + balanceBefore + "，需要: " + amount);
        }

        BigDecimal balanceAfter = balanceBefore.subtract(amount);
        user.setBalance(balanceAfter);
        userRepository.save(user);

        BalanceLog balanceLog = new BalanceLog();
        balanceLog.setUserId(userId);
        balanceLog.setType("CONSUME");
        balanceLog.setAmount(amount);
        balanceLog.setBalanceBefore(balanceBefore);
        balanceLog.setBalanceAfter(balanceAfter);
        balanceLog.setOrderId(orderId);
        balanceLog.setRemark(remark);
        balanceLogRepository.save(balanceLog);

        log.info("用户 {} 消费 {}，订单 {}，余额 {} -> {}", userId, amount, orderId, balanceBefore, balanceAfter);
        return balanceAfter;
    }

    @Transactional
    public BigDecimal refund(Long userId, BigDecimal amount, Long orderId, String remark) {
        User user = getUser(userId);
        BigDecimal balanceBefore = user.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(amount);
        user.setBalance(balanceAfter);
        userRepository.save(user);

        BalanceLog balanceLog = new BalanceLog();
        balanceLog.setUserId(userId);
        balanceLog.setType("REFUND");
        balanceLog.setAmount(amount);
        balanceLog.setBalanceBefore(balanceBefore);
        balanceLog.setBalanceAfter(balanceAfter);
        balanceLog.setOrderId(orderId);
        balanceLog.setRemark(remark);
        balanceLogRepository.save(balanceLog);

        log.info("用户 {} 退款 {}，订单 {}，余额 {} -> {}", userId, amount, orderId, balanceBefore, balanceAfter);
        return balanceAfter;
    }

    public PageResponse<BalanceLog> getLogs(Long userId, PageRequest request) {
        List<BalanceLog> allLogs = balanceLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
        int total = allLogs.size();
        int page = Math.max(request.getPage(), 1);
        int size = Math.max(request.getSize(), 1);
        int fromIndex = (page - 1) * size;

        if (fromIndex >= total) {
            return new PageResponse<>(List.of(), total, page, size);
        }

        int toIndex = Math.min(fromIndex + size, total);
        List<BalanceLog> pageList = allLogs.subList(fromIndex, toIndex);
        return new PageResponse<>(pageList, total, page, size);
    }

    public BigDecimal getBalance(Long userId) {
        User user = getUser(userId);
        return user.getBalance();
    }
}
