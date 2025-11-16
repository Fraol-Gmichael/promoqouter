package com.fraolgmichael.promoqouter.promotion.service.impl;

import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PromotionEngineTieredBulkDiscountImplTest {

    private PromotionEngineTieredBulkDiscountImpl engine;

    @BeforeEach
    void setUp() {
        engine = new PromotionEngineTieredBulkDiscountImpl(null);
    }

    @Test
    void validateUseCaseSpecific_shouldReturnError_whenTieredInfosIsNull() {
        Promotion promotion = new Promotion();
        promotion.setTieredInfos(null);

        List<String> errors = engine.validateUseCaseSpecific(promotion);

        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("tieredInfos is required"));
    }

    @Test
    void validateUseCaseSpecific_shouldReturnError_whenTieredInfosIsEmpty() {
        Promotion promotion = new Promotion();
        promotion.setTieredInfos(List.of());

        List<String> errors = engine.validateUseCaseSpecific(promotion);

        assertEquals(1, errors.size());
        assertTrue(errors.getFirst().contains("tieredInfos is required"));
    }

    @Test
    void shouldBypassTargetValidation() {
        Promotion promotion = new Promotion();
        assertTrue(engine.validateTarget(promotion).isEmpty());
    }

    @Test
    void validateUseCaseSpecific_shouldReturnError_whenFromOrToIsNull() {
        Promotion promotion = new Promotion();
        promotion.setTieredInfos(List.of(
                new Promotion.TieredInfo(null, 5, BigDecimal.TEN),
                new Promotion.TieredInfo(0, null, BigDecimal.TEN)
        ));

        List<String> errors = engine.validateUseCaseSpecific(promotion);

        assertEquals(2, errors.size());
        assertTrue(errors.get(0).contains("must not be null"));
        assertTrue(errors.get(1).contains("must not be null"));
    }

    @Test
    void validateUseCaseSpecific_shouldReturnError_whenFromOrToNegative() {
        Promotion promotion = new Promotion();
        promotion.setTieredInfos(List.of(
                new Promotion.TieredInfo(-1, 5, BigDecimal.TEN),
                new Promotion.TieredInfo(0, -2, BigDecimal.TEN)
        ));

        List<String> errors = engine.validateUseCaseSpecific(promotion);

        assertEquals(3, errors.size());
        assertTrue(errors.get(0).contains("must be >= 0"));
        assertTrue(errors.get(1).contains("must be >= 0"));
        assertTrue(errors.get(2).contains("cannot be greater than toValue"));
    }

    @Test
    void validateUseCaseSpecific_shouldReturnError_whenFromGreaterThanTo() {
        Promotion promotion = new Promotion();
        promotion.setTieredInfos(List.of(
                new Promotion.TieredInfo(5, 3, BigDecimal.TEN)
        ));

        List<String> errors = engine.validateUseCaseSpecific(promotion);

        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("cannot be greater than toValue"));
    }

    @Test
    void validateUseCaseSpecific_shouldReturnError_whenTiersOverlap() {
        Promotion promotion = new Promotion();
        promotion.setTieredInfos(List.of(
                new Promotion.TieredInfo(0, 5, BigDecimal.TEN),
                new Promotion.TieredInfo(4, 8, BigDecimal.valueOf(15))
        ));

        List<String> errors = engine.validateUseCaseSpecific(promotion);

        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("overlaps"));
    }

    @Test
    void validateUseCaseSpecific_shouldPass_whenTiersAreValidAndNonOverlapping() {
        Promotion promotion = new Promotion();
        promotion.setTieredInfos(List.of(
                new Promotion.TieredInfo(0, 2, BigDecimal.TEN),
                new Promotion.TieredInfo(3, 5, BigDecimal.valueOf(15)),
                new Promotion.TieredInfo(6, 10, BigDecimal.valueOf(20))
        ));

        List<String> errors = engine.validateUseCaseSpecific(promotion);

        assertTrue(errors.isEmpty());
    }

}
