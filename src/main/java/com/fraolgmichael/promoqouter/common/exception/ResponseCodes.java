package com.fraolgmichael.promoqouter.common.exception;

public class ResponseCodes {
    public static final ResponseCode BAD_REQUEST =
            ResponseCode.builder().code(400).internalCode("400-0").message("Bad request").build();
    public static final ResponseCode NOT_FOUND =
            ResponseCode.builder().code(404).internalCode("404-0").message("Not found").build();
    public static final ResponseCode ALREADY_EXISTS =
            ResponseCode.builder().code(409).internalCode("409-0").message("Already exists").build();

    private ResponseCodes() {
        // don't initialize me
    }

}
