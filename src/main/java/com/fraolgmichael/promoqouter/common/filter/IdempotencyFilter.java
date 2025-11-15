package com.fraolgmichael.promoqouter.common.filter;

import com.fraolgmichael.promoqouter.common.dto.CachedResponse;
import com.fraolgmichael.promoqouter.common.service.IdempotencyCacheService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotencyFilter extends OncePerRequestFilter {

    public static final String X_IDEMPOTENCY_KEY = "X-Idempotency-Key";
    private final IdempotencyCacheService cacheService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            FilterChain filterChain) throws ServletException, IOException {

        String key = httpRequest.getHeader(X_IDEMPOTENCY_KEY);

        if (key != null && cacheService.exists(key)) {
            CachedResponse cached = cacheService.get(key);

            httpResponse.setStatus(cached.getStatus());
            cached.getHeaders().forEach(httpResponse::setHeader);

            httpResponse.getOutputStream().write(cached.getBody());
            log.info("Found cached response for key: {}", key);
            return;
        }

        ContentCachingResponseWrapper responseWrapper =
                new ContentCachingResponseWrapper(httpResponse);

        filterChain.doFilter(httpRequest, responseWrapper);

        byte[] body = responseWrapper.getContentAsByteArray();

        if (key != null) {
            cacheService.save(
                    key,
                    new CachedResponse(
                            responseWrapper.getStatus(),
                            extractHeaders(responseWrapper),
                            body
                    )
            );
        }

        responseWrapper.copyBodyToResponse();
    }

    private Map<String, String> extractHeaders(ContentCachingResponseWrapper responseWrapper) {
        Map<String, String> headers = new HashMap<>();
        for (String name : responseWrapper.getHeaderNames()) {
            headers.put(name, responseWrapper.getHeader(name));
        }
        return headers;
    }
}
