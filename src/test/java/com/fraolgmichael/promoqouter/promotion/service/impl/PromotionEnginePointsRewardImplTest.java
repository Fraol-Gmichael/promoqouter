package com.fraolgmichael.promoqouter.promotion.service.impl;

import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PromotionEnginePointsRewardImplTest {

    PromotionEnginePointsRewardImpl engine = new PromotionEnginePointsRewardImpl(null);

    @Test
    void promotionType_shouldBePOINTS_REWARD() {
        assertEquals(Promotion.Type.POINTS_REWARD, engine.promotionType());
    }

    @Test
    void shouldGetError_whenPointsToRewardIsNotPresent() {
        Promotion promotion = Promotion.builder().build();

        List<String> errors = engine.validateUseCaseSpecific(promotion);

        assertEquals(1, errors.size());
        assertTrue(errors.stream().anyMatch(err ->
                err.contains("pointsToReward is required")
        ));
    }

    @Test
    void noError_whenPointsToRewardIsPresent() {
        Promotion promotion = Promotion.builder()
                .pointsToReward(BigDecimal.ZERO)
                .build();

        List<String> errors = engine.validateUseCaseSpecific(promotion);

        assertEquals(0, errors.size());
    }
}
