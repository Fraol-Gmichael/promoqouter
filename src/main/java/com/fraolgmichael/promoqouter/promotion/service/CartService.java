package com.fraolgmichael.promoqouter.promotion.service;

import com.fraolgmichael.promoqouter.promotion.dto.CartRequestDto;
import com.fraolgmichael.promoqouter.promotion.dto.CartResponseDto;
import jakarta.validation.Valid;

public interface CartService {
    CartResponseDto quote(@Valid CartRequestDto cartRequestDto);

    CartResponseDto confirm(@Valid CartRequestDto cartRequestDto);
}
