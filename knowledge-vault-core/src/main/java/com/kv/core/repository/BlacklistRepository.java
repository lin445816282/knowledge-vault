package com.kv.core.repository;

import com.kv.core.entity.Blacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlacklistRepository extends JpaRepository<Blacklist, Long> {

    Optional<Blacklist> findByTargetTypeAndTargetValue(String targetType, String targetValue);

    List<Blacklist> findByTargetValue(String targetValue);
}
