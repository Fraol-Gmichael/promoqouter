package com.fraolgmichael.promoqouter.promotion.service.impl;

import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PromotionEngineShippingWaiverImplTest {

    PromotionEngineShippingWaiverImpl engine = new PromotionEngineShippingWaiverImpl(null);

    @Test
    void promotionType_shouldBeFREE_SHIPPING() {
        assertEquals(Promotion.Type.FREE_SHIPPING, engine.promotionType());
    }

    @Test
    void noError_forFreeShippingPromotion() {
        Promotion promotion = Promotion.builder().build();

        List<String> errors = engine.validateUseCaseSpecific(promotion);

        assertEquals(0, errors.size());
    }
}
