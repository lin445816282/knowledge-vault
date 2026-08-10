package com.kv.core.repository;

import com.kv.core.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByBuyerId(Long buyerId);

    List<Order> findBySellerId(Long sellerId);

    Optional<Order> findByKnowledgeIdAndBuyerId(Long knowledgeId, Long buyerId);
}
