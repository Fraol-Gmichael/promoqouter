package com.fraolgmichael.promoqouter.common.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ServiceException extends RuntimeException {

    private final String detailMessage;

    private final ResponseCode responseCode;

    private Map<String, String> additionalInfo = new HashMap<>();

    public ServiceException(ResponseCode responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
        this.detailMessage = message;
    }

    public ServiceException(ResponseCode responseCode, String message, Map<String, String> additionalInfo) {
        super(message);
        this.responseCode = responseCode;
        this.detailMessage = message;
        this.additionalInfo = additionalInfo;
    }
}
