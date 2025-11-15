package com.fraolgmichael.promoqouter.common.service.impl;

import com.fraolgmichael.promoqouter.common.dto.CachedResponse;
import com.fraolgmichael.promoqouter.common.service.IdempotencyCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class IdempotencyCacheServiceHashmapImplTest {

    private IdempotencyCacheService service;

    @BeforeEach
    void setup() {
        service = new IdempotencyCacheServiceHashmapImpl();
    }

    @Test
    void saveAndGet_shouldReturnSavedValue() {
        CachedResponse resp = new CachedResponse(200, Map.of("H", "V"), "abc".getBytes());

        service.save("k1", resp);

        CachedResponse result = service.get("k1");

        assertNotNull(result);
        assertEquals(200, result.getStatus());
        assertEquals("V", result.getHeaders().get("H"));
        assertEquals("abc", new String(result.getBody()));
    }

    @Test
    void exists_shouldReflectStoredKeys() {
        assertFalse(service.exists("k2"));

        service.save("k2", new CachedResponse(201, Map.of(), new byte[0]));

        assertTrue(service.exists("k2"));
    }

    @Test
    void overwrite_shouldReplaceOldValue() {
        CachedResponse r1 = new CachedResponse(200, Map.of("A", "1"), "one".getBytes());
        CachedResponse r2 = new CachedResponse(500, Map.of("B", "2"), "two".getBytes());

        service.save("k3", r1);
        service.save("k3", r2);

        CachedResponse result = service.get("k3");

        assertEquals(500, result.getStatus());
        assertEquals("2", result.getHeaders().get("B"));
        assertEquals("two", new String(result.getBody()));
    }

    @Test
    void get_nonExistingKeyShouldReturnNull() {
        assertNull(service.get("no-key"));
    }
}
