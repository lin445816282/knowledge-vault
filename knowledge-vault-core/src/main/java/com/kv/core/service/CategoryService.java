package com.kv.core.service;

import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.common.exception.BusinessException;
import com.kv.core.entity.Category;
import com.kv.core.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public PageResponse<Category> list(PageRequest request) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                request.getPage() - 1,
                request.getSize(),
                Sort.by(Sort.Direction.ASC, "sortOrder"));
        Page<Category> page = categoryRepository.findAll(pageable);
        return new PageResponse<>(page.getContent(), page.getTotalElements(), request.getPage(), request.getSize());
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "分类不存在"));
    }

    @Transactional
    public Category create(Category entity) {
        return categoryRepository.save(entity);
    }

    @Transactional
    public Category update(Long id, Category dto) {
        Category existing = getById(id);
        existing.setName(dto.getName());
        existing.setIcon(dto.getIcon());
        existing.setDescription(dto.getDescription());
        existing.setSortOrder(dto.getSortOrder());
        return categoryRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Category category = getById(id);
        if (categoryRepository.countByParentId(id) > 0) {
            throw new BusinessException(400, "该分类下存在子分类，无法删除");
        }
        categoryRepository.delete(category);
    }

    /**
     * 获取全部分类树结构
     */
    public List<TreeNode> getTree() {
        List<Category> all = categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "sortOrder"));

        Map<Long, TreeNode> nodeMap = new HashMap<>();
        List<TreeNode> roots = new ArrayList<>();

        // 第一遍：创建所有节点
        for (Category cat : all) {
            TreeNode node = new TreeNode();
            node.setId(cat.getId());
            node.setName(cat.getName());
            node.setParentId(cat.getParentId());
            node.setLevel(cat.getLevel());
            node.setIcon(cat.getIcon());
            node.setChildren(new ArrayList<>());
            nodeMap.put(cat.getId(), node);
        }

        // 第二遍：组装父子关系
        for (Category cat : all) {
            TreeNode node = nodeMap.get(cat.getId());
            if (cat.getParentId() == null) {
                roots.add(node);
            } else {
                TreeNode parent = nodeMap.get(cat.getParentId());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            }
        }

        return roots;
    }

    @Data
    public static class TreeNode {
        private Long id;
        private String name;
        private Long parentId;
        private Integer level;
        private String icon;
        private List<TreeNode> children;
    }
}
