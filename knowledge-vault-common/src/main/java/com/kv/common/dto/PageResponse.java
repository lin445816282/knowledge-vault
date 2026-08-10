package com.kv.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 通用分页响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> list;
    private long total;
    private int page;
    private int size;

    public long getTotalPages() {
        return size > 0 ? (total + size - 1) / size : 0;
    }
}
