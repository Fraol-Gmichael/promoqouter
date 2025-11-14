package com.fraolgmichael.promoqouter.common.exception;

public class ResponseCodes {
    public static final ResponseCode BAD_REQUEST =
            ResponseCode.builder().code(400).internalCode("400-0").message("Bad request").build();

    private ResponseCodes() {
        // don't initialize me
    }

}
