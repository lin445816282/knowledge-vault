package com.kv.core.repository;

import com.kv.core.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByParentIdOrderBySortOrder(Long parentId);

    List<Category> findByLevel(Integer level);

    long countByParentId(Long parentId);
}
