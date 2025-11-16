package com.fraolgmichael.promoqouter.promotion.service.impl;

import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PromotionEnginePercentOFFImplTest {

    PromotionEnginePercentOFFImpl engine = new PromotionEnginePercentOFFImpl(null);

    @Test
    void promotionType_shouldBePERCENT_OFF() {
        assertEquals(Promotion.Type.PERCENT_OFF, engine.promotionType());
    }

    @Test
    void shouldGetError_whenPercentOffAmountIsNotPresent() {
        Promotion promotion = Promotion.builder().build();

        List<String> errors = engine.validateUseCaseSpecific(promotion);

        assertEquals(1, errors.size());
        assertTrue(errors.stream().anyMatch(error ->
                error.contains("percentOffAmount is required")
        ));
    }

    @Test
    void noError_whenPercentOffAmountIsPresent() {
        Promotion promotion = Promotion.builder()
                .percentOffAmount(BigDecimal.TEN)
                .build();

        List<String> errors = engine.validateUseCaseSpecific(promotion);

        assertEquals(0, errors.size());
    }
}
