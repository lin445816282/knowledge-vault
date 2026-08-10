package com.kv.core.repository;

import com.kv.core.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByBuyerId(Long buyerId);

    List<Order> findBySellerId(Long sellerId);

    Page<Order> findByBuyerId(Long buyerId, Pageable pageable);

    Page<Order> findBySellerId(Long sellerId, Pageable pageable);

    Optional<Order> findByKnowledgeIdAndBuyerId(Long knowledgeId, Long buyerId);

    Optional<Order> findByKnowledgeIdAndBuyerIdAndStatus(Long knowledgeId, Long buyerId, String status);
}
