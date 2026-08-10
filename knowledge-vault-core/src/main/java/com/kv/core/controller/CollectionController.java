package com.kv.core.controller;

import com.kv.common.dto.ApiResponse;
import com.kv.common.dto.PageRequest;
import com.kv.core.entity.UserCollect;
import com.kv.core.service.CollectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @GetMapping
    public ApiResponse<List<UserCollect>> list(PageRequest pageRequest) {
        log.info("GET /api/v1/collections - list collections, page={}, size={}", pageRequest.getPage(), pageRequest.getSize());
        return ApiResponse.ok(collectionService.list(pageRequest));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserCollect> getById(@PathVariable Long id) {
        log.info("GET /api/v1/collections/{}", id);
        return ApiResponse.ok(collectionService.getById(id));
    }

    @PostMapping
    public ApiResponse<UserCollect> create(@RequestBody UserCollect collection) {
        log.info("POST /api/v1/collections - create collection");
        return ApiResponse.ok(collectionService.create(collection));
    }

    @PutMapping("/{id}")
    public ApiResponse<UserCollect> update(@PathVariable Long id, @RequestBody UserCollect collection) {
        log.info("PUT /api/v1/collections/{} - update collection", id);
        return ApiResponse.ok(collectionService.update(id, collection));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/v1/collections/{}", id);
        collectionService.delete(id);
        return ApiResponse.ok();
    }
}
