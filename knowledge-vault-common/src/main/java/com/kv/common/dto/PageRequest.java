package com.kv.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用分页请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {
    private int page = 1;
    private int size = 20;
    private String sort;
}
