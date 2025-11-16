package com.fraolgmichael.promoqouter.promotion.controller;

import com.fraolgmichael.promoqouter.common.dto.ApiResponse;
import com.fraolgmichael.promoqouter.promotion.dto.CartRequestDto;
import com.fraolgmichael.promoqouter.promotion.dto.CartResponseDto;
import com.fraolgmichael.promoqouter.promotion.service.CartService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    @PostMapping("/quote")
    public ApiResponse<CartResponseDto> quote(
            @NotNull @RequestBody CartRequestDto cartRequestDto
    ) {
        return ApiResponse.ok(cartService.quote(cartRequestDto));
    }

    @PostMapping("/confirm")
    public ApiResponse<CartResponseDto> confirm(
            @NotNull @RequestBody CartRequestDto cartRequestDto
    ) {
        return ApiResponse.ok(cartService.confirm(cartRequestDto));
    }
}
