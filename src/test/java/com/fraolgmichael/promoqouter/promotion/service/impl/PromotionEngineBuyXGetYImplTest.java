package com.fraolgmichael.promoqouter.promotion.service.impl;

import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import org.junit.jupiter.api.Test;

import java.util.List;

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
}
