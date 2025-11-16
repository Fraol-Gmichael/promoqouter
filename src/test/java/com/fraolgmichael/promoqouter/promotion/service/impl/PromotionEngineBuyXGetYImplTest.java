package com.fraolgmichael.promoqouter.promotion.service.impl;

import com.fraolgmichael.promoqouter.product.service.Category;
import com.fraolgmichael.promoqouter.product.service.Product;
import com.fraolgmichael.promoqouter.promotion.dto.Cart;
import com.fraolgmichael.promoqouter.promotion.dto.CartRequestDto;
import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PromotionEngineBuyXGetYImplTest {

    PromotionEngineBuyXGetYImpl engine = new PromotionEngineBuyXGetYImpl(null);

    @Test
    void promotionType_shouldBeBUY_X_GET_Y() {
        assertEquals(Promotion.Type.BUY_X_GET_Y, engine.promotionType());
    }

    @Test
    void shouldGetTwoErrors_whenBuyXAmountAndGetYAmountAreNotPresent() {
        Promotion promotion = Promotion.builder().build();
        List<String> errors = engine.validateUseCaseSpecific(promotion);
        assertEquals(2, errors.size());
        assertTrue(errors.stream().anyMatch(error -> error.contains("getYAmount is required")));
        assertTrue(errors.stream().anyMatch(error -> error.contains("buyXAmount is required")));
    }

    @Test
    void noError_whenBuyXAmountAndGetYAmountArePresent() {
        Promotion promotion = Promotion.builder().getYAmount(1).buyXAmount(2).build();

        List<String> errors = engine.validateUseCaseSpecific(promotion);
        assertEquals(0, errors.size());
    }

    @Test
    void shouldReturnImmediately_whenTotalPriceIsZero() {
        Promotion promotion = Promotion.builder().getYAmount(1).buyXAmount(2).build();

        Cart cartIn = Cart.builder().totalPrice(BigDecimal.ZERO).build();
        Cart cart = engine.apply(promotion, List.of(), CartRequestDto.builder().build(), cartIn);
        assertEquals(cart, cartIn);
    }

    @Test
    void success() {
        Promotion promotion = Promotion.builder().getYAmount(1).buyXAmount(2)
                .targetCategory(Category.GROCERIES)
                .target(Promotion.Target.CATEGORY)
                .build();
        UUID productId = UUID.randomUUID();

        Product product = Product.builder().id(productId).category(Category.GROCERIES)
                .price(BigDecimal.valueOf(10)).build();
        CartRequestDto.CartItem cartItem = CartRequestDto.CartItem.builder().qty(2L).productId(productId).build();
        Cart cartIn = Cart.builder().totalPrice(BigDecimal.valueOf(100)).productDiscountInfos(
                Map.of(productId, Cart.ProductDiscountInfo.builder()
                        .name(product.getName())
                        .product(product)
                        .cartItem(cartItem)
                        .discounts(new ArrayList<>())
                        .build())
        ).build();
        List<Product> products = List.of(product);
        List<CartRequestDto.CartItem> items = List.of(cartItem);
        Cart cart = engine.apply(promotion, products, CartRequestDto.builder()
                .items(items).build(), cartIn);
        assertEquals(BigDecimal.valueOf(90), cart.getTotalPrice());

    }

    @Test
    void success_zeroTotal() {
        Promotion promotion = Promotion.builder().getYAmount(1000).buyXAmount(1)
                .targetCategory(Category.GROCERIES)
                .target(Promotion.Target.CATEGORY)
                .build();
        UUID productId = UUID.randomUUID();

        Product product = Product.builder().id(productId).category(Category.GROCERIES)
                .price(BigDecimal.valueOf(10)).build();
        CartRequestDto.CartItem cartItem = CartRequestDto.CartItem.builder().qty(2L).productId(productId).build();
        Cart cartIn = Cart.builder().totalPrice(BigDecimal.valueOf(100)).productDiscountInfos(
                Map.of(productId, Cart.ProductDiscountInfo.builder()
                        .name(product.getName())
                        .product(product)
                        .cartItem(cartItem)
                        .discounts(new ArrayList<>())
                        .build())
        ).build();
        List<Product> products = List.of(product);
        List<CartRequestDto.CartItem> items = List.of(cartItem);
        Cart cart = engine.apply(promotion, products, CartRequestDto.builder()
                .items(items).build(), cartIn);
        assertEquals(BigDecimal.valueOf(80), cart.getTotalPrice());

    }
}
