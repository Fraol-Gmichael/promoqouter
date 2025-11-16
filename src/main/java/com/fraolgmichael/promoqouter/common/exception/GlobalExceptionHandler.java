package com.fraolgmichael.promoqouter.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fraolgmichael.promoqouter.common.dto.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    protected static String extractMessage(HttpMessageNotReadableException ex, Throwable rootCause) {
        if (rootCause instanceof InvalidFormatException) {
            return "One or more fields have invalid data types. Please verify your request (invalid data type getting passed).";
        }
        if (rootCause instanceof MismatchedInputException) {
            return "Malformed or missing input. Ensure the request body matches the expected structure.";
        }
        if (ex.getMessage() != null && ex.getMessage().contains("Required request body is missing")) {
            return "Request body is missing. Please include a valid JSON payload.";
        }
        return "Invalid or unreadable request body.";
    }

    private static Throwable getRootCause(Throwable ex) {
        Throwable cause = ex;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }

    public static HttpStatus resolveHttpStatus(int code) {
        try {
            return HttpStatus.valueOf(code);
        } catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    public static void traceAndLogExceptionOrigin(Exception exception) {
        StackTraceElement[] stackTrace = exception.getStackTrace();
        if (stackTrace.length > 0) {
            StackTraceElement element = stackTrace[0];
            String className = element.getClassName();
            int lineNumber = element.getLineNumber();
            log.error("Exception thrown in class: {}, at line: {}", className, lineNumber);
            if (!(exception instanceof ServiceException)) {
                log.error(exception.getMessage(), exception);
            }
        }
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handle(ConstraintViolationException ex) {
        return new ResponseEntity<>(ApiResponse.error(ResponseCodes.BAD_REQUEST,
                Map.of("errorDetail", collectConstraintViolations(ex))), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiResponse<?>> handle(ServiceException ex) {
        return createErrorResponse(ex);
    }

    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        traceAndLogExceptionOrigin(exception);

        Throwable rootCause = getRootCause(exception);
        String message = extractMessage(exception, rootCause);
        return new ResponseEntity<>(ApiResponse.error(ResponseCodes.BAD_REQUEST,
                Map.of("errorDetail", message)), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ApiResponse<?>> createErrorResponse(ServiceException ex) {
        traceAndLogExceptionOrigin(ex);
        ResponseCode responseCode = ex.getResponseCode();
        return new ResponseEntity<>(ApiResponse.error(responseCode,
                Map.of("errorDetail", ex.getDetailMessage())), resolveHttpStatus(responseCode.code()));
    }

    private String collectConstraintViolations(ConstraintViolationException exception) {
        return exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
    }
}