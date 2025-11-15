package com.fraolgmichael.promoqouter.common.filter;

import com.fraolgmichael.promoqouter.common.dto.CachedResponse;
import com.fraolgmichael.promoqouter.common.service.IdempotencyCacheService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.util.Map;

import static com.fraolgmichael.promoqouter.common.filter.IdempotencyFilter.X_IDEMPOTENCY_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdempotencyFilterTest {

    @Mock
    private IdempotencyCacheService cacheService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private IdempotencyFilter filter;

    @Test
    void whenKeyExists_shouldReturnCachedResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader(X_IDEMPOTENCY_KEY, "k1");

        CachedResponse cached = new CachedResponse(
                200,
                Map.of("X-Test", "abc"),
                "cached-body".getBytes()
        );

        when(cacheService.exists("k1")).thenReturn(true);
        when(cacheService.get("k1")).thenReturn(cached);

        filter.doFilter(request, response, filterChain);

        assertEquals(200, response.getStatus());
        assertEquals("abc", response.getHeader("X-Test"));
        assertEquals("cached-body", response.getContentAsString());
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void whenKeyIsNull_shouldExecuteChainAndSaveNothing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse rawResponse = new MockHttpServletResponse();

        doAnswer(invocation -> {
            ContentCachingResponseWrapper resp = invocation.getArgument(1);
            resp.setStatus(201);
            resp.getWriter().write("normal-body");
            return null;
        }).when(filterChain).doFilter(any(), any());

        filter.doFilter(request, rawResponse, filterChain);

        assertEquals(201, rawResponse.getStatus());
        assertEquals("normal-body", rawResponse.getContentAsString());
        verify(cacheService, never()).save(any(), any());
    }

    @Test
    void whenKeyPresentButNotExisting_shouldExecuteChainAndSaveResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse rawResponse = new MockHttpServletResponse();
        request.addHeader(X_IDEMPOTENCY_KEY, "k2");

        when(cacheService.exists("k2")).thenReturn(false);

        doAnswer(invocation -> {
            ContentCachingResponseWrapper resp = invocation.getArgument(1);
            resp.setStatus(202);
            resp.getWriter().write("generated-body");
            return null;
        }).when(filterChain).doFilter(any(), any());

        filter.doFilter(request, rawResponse, filterChain);

        assertEquals(202, rawResponse.getStatus());
        assertEquals("generated-body", rawResponse.getContentAsString());

        verify(cacheService).save(
                eq("k2"),
                argThat(cr ->
                        cr.getStatus() == 202 &&
                                new String(cr.getBody()).equals("generated-body")
                )
        );
    }

    @Test
    void whenSavingResponse_shouldExtractHeadersCorrectly() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse rawResponse = new MockHttpServletResponse();
        request.addHeader(X_IDEMPOTENCY_KEY, "k3");

        when(cacheService.exists("k3")).thenReturn(false);

        doAnswer(invocation -> {
            ContentCachingResponseWrapper resp = invocation.getArgument(1);
            resp.setStatus(203);
            resp.addHeader("X-H1", "v1");
            resp.addHeader("X-H2", "v2");
            resp.getWriter().write("header-body");
            return null;
        }).when(filterChain).doFilter(any(), any());

        filter.doFilter(request, rawResponse, filterChain);

        assertEquals(203, rawResponse.getStatus());
        assertEquals("header-body", rawResponse.getContentAsString());

        verify(cacheService).save(
                eq("k3"),
                argThat(cr ->
                        cr.getStatus() == 203 &&
                                new String(cr.getBody()).equals("header-body") &&
                                cr.getHeaders().size() >= 2 &&
                                cr.getHeaders().get("X-H1").equals("v1") &&
                                cr.getHeaders().get("X-H2").equals("v2")
                )
        );
    }

}