package com.kv.core.service;

import com.kv.common.dto.PageRequest;
import com.kv.common.dto.PageResponse;
import com.kv.core.entity.SystemConfig;
import com.kv.core.repository.SystemConfigRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemConfigService {

    private final SystemConfigRepository systemConfigRepository;

    private final ConcurrentMap<String, String> cache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        refreshCache();
        log.info("SystemConfig cache initialized with {} entries", cache.size());
    }

    /**
     * Get config value by key, or null if not found. Uses in-memory cache.
     */
    public String get(String key) {
        return cache.computeIfAbsent(key, k -> {
            return systemConfigRepository.findByConfigKey(k)
                    .map(SystemConfig::getConfigValue)
                    .orElse(null);
        });
    }

    /**
     * Get config value by key, returning defaultValue if not found.
     */
    public String get(String key, String defaultValue) {
        String value = get(key);
        return value != null ? value : defaultValue;
    }

    /**
     * Get config value as boolean, returning defaultValue if not found or unparseable.
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        if (value == null) {
            return defaultValue;
        }
        if ("true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value) || "0".equals(value) || "no".equalsIgnoreCase(value)) {
            return false;
        }
        return defaultValue;
    }

    /**
     * Set a config value. Creates the config if it doesn't exist, updates if it does.
     * Refreshes the cache entry afterwards.
     */
    @Transactional
    public SystemConfig set(String key, String value, String description) {
        SystemConfig config = systemConfigRepository.findByConfigKey(key)
                .orElseGet(SystemConfig::new);

        config.setConfigKey(key);
        config.setConfigValue(value);
        config.setDescription(description);

        SystemConfig saved = systemConfigRepository.save(config);

        // Refresh cache
        cache.put(key, value);
        log.debug("SystemConfig '{}' set to '{}'", key, value);

        return saved;
    }

    /**
     * Get all system configs (from database, not cache).
     */
    public List<SystemConfig> getAll() {
        return systemConfigRepository.findAll();
    }

    /**
     * Paginated listing of all system configs.
     */
    public PageResponse<SystemConfig> listAll(PageRequest request) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                request.getPage() - 1,
                request.getSize(),
                Sort.by(Sort.Direction.ASC, "configKey"));
        Page<SystemConfig> page = systemConfigRepository.findAll(pageable);
        return new PageResponse<>(page.getContent(), page.getTotalElements(), request.getPage(), request.getSize());
    }

    /**
     * Full cache refresh from database.
     */
    public void refreshCache() {
        List<SystemConfig> all = systemConfigRepository.findAll();
        cache.clear();
        for (SystemConfig config : all) {
            if (config.getConfigKey() != null) {
                cache.put(config.getConfigKey(), config.getConfigValue());
            }
        }
        log.debug("Cache refreshed: {} entries loaded", cache.size());
    }

    /**
     * Invalidate a single cache entry.
     */
    public void evictCache(String key) {
        cache.remove(key);
    }
}
