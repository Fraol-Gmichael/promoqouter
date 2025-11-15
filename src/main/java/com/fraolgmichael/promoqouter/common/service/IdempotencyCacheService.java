package com.fraolgmichael.promoqouter.common.service;

import com.fraolgmichael.promoqouter.common.dto.CachedResponse;

public interface IdempotencyCacheService {

    boolean exists(String key);

    CachedResponse get(String key);

    void save(String key, CachedResponse response);
}
