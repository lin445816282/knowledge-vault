package com.kv.core.repository;

import com.kv.core.entity.BalanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BalanceLogRepository extends JpaRepository<BalanceLog, Long> {

    List<BalanceLog> findByUserIdOrderByCreatedAtDesc(Long userId);
}
