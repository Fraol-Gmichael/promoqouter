package com.fraolgmichael.promoqouter.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fraolgmichael.promoqouter.common.exception.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private String requestId;

    private String apiVersion;

    private String path;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    @Builder.Default
    private boolean success = true;

    private String internalCode;

    private String message;

    private T data;

    private Map<String, String> additionalInfo;

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .internalCode("200")
                .message("OK")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .internalCode("201")
                .message("Created")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(ResponseCode responseCode, Map<String, String> additionalInfo) {
        return ApiResponse.<T>builder()
                .success(false)
                .internalCode(responseCode.internalCode())
                .message(responseCode.message())
                .additionalInfo(additionalInfo)
                .build();
    }

}