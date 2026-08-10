package com.kv.core.entity;

import com.kv.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "categories")
public class Category extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    /** 父分类ID，null=一级分类 */
    private Long parentId;

    /** 层级深度：1=行业, 2=领域, 3=岗位, ... */
    @Column(nullable = false)
    private Integer level = 1;

    /** 祖先路径：/1/5/12 */
    @Column(length = 500)
    private String path;

    /** 排序 */
    private Integer sortOrder = 0;

    @Column(length = 200)
    private String icon;

    @Column(length = 500)
    private String description;
}
