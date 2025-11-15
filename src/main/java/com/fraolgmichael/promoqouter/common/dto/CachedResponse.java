package com.fraolgmichael.promoqouter.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class CachedResponse {
    private int status;
    private Map<String, String> headers;
    private byte[] body;
}
