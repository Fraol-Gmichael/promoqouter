package com.fraolgmichael.promoqouter.promotion.service;

import com.fraolgmichael.promoqouter.promotion.dto.Cart;
import com.fraolgmichael.promoqouter.promotion.dto.CartRequestDto;
import jakarta.validation.Valid;

public interface CartService {
    Cart quote(@Valid CartRequestDto cartRequestDto);

    Cart confirm(@Valid CartRequestDto cartRequestDto);
}
