package com.fraolgmichael.promoqouter.promotion.service.impl;

import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PromotionEngineFixedDiscountImplTest {

    PromotionEngineFixedDiscountImpl engine = new PromotionEngineFixedDiscountImpl(null);

    @Test
    void promotionType_shouldBeFIXED_DISCOUNT() {
        assertEquals(Promotion.Type.FIXED_DISCOUNT, engine.promotionType());
    }

    @Test
    void shouldGetError_whenDiscountAmountIsNotPresent() {
        Promotion promotion = Promotion.builder().build();

        List<String> errors = engine.validateUseCaseSpecific(promotion);

        assertEquals(1, errors.size());
        assertTrue(errors.stream().anyMatch(error ->
                error.contains("discountAmount is required")
        ));
    }

    @Test
    void noError_whenDiscountAmountIsPresent() {
        Promotion promotion = Promotion.builder()
                .discountAmount(BigDecimal.TEN)
                .build();

        List<String> errors = engine.validateUseCaseSpecific(promotion);

        assertEquals(0, errors.size());
    }
}
