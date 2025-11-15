package com.fraolgmichael.promoqouter.common.service.impl;

import com.fraolgmichael.promoqouter.common.dto.CachedResponse;
import com.fraolgmichael.promoqouter.common.service.IdempotencyCacheService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IdempotencyCacheServiceHashmapImpl implements IdempotencyCacheService {

    private final Map<String, CachedResponse> cache = new ConcurrentHashMap<>();

    @Override
    public CachedResponse get(String key) {
        return cache.get(key);
    }

    @Override
    public void save(String key, CachedResponse response) {
        cache.put(key, response);
    }

    @Override
    public boolean exists(String key) {
        return cache.containsKey(key);
    }
}
