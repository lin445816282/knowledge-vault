package com.kv.core.repository;

import com.kv.core.entity.ShareLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShareLogRepository extends JpaRepository<ShareLog, Long> {

    Optional<ShareLog> findByShareToken(String shareToken);

    List<ShareLog> findBySharerId(Long sharerId);
}
