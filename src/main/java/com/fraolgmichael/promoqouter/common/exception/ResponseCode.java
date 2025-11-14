package com.fraolgmichael.promoqouter.common.exception;

import lombok.Builder;

@Builder(toBuilder = true)
public record ResponseCode(
        int code,
        String internalCode,
        String message
) {
}
