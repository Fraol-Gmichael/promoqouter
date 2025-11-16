package com.fraolgmichael.promoqouter.promotion.controller;

import com.fraolgmichael.promoqouter.promotion.dto.CartRequestDto;
import com.fraolgmichael.promoqouter.promotion.service.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class CartControllerTest {
    @Mock
    CartService cartService;
    @InjectMocks
    CartController cartController;

    @Test
    void qoute() {
        cartController.quote(CartRequestDto.builder().build());
        verify(cartService).quote(any());
    }

    @Test
    void confirm() {
        cartController.confirm(CartRequestDto.builder().build());
        verify(cartService).confirm(any());
    }
}