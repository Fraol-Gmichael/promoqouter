package com.fraolgmichael.promoqouter.promotion.service.impl;

import com.fraolgmichael.promoqouter.promotion.dataaccess.PromotionEntity;
import com.fraolgmichael.promoqouter.promotion.dataaccess.PromotionRepository;
import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PromotionEnginePromoCodeImplTest {

    private static final UUID VALID_PROMOTION_ID = UUID.randomUUID();
    private static final UUID INVALID_PROMOTION_ID = UUID.randomUUID();

    private final PromotionRepository promotionRepository = mock(PromotionRepository.class);
    private final PromotionEnginePromoCodeImpl engine = new PromotionEnginePromoCodeImpl(null, promotionRepository);

    @Test
    void promotionType_shouldBePROMO_CODE() {
        assertEquals(Promotion.Type.PROMO_CODE, engine.promotionType());
    }

    @Test
    void shouldGetTwoErrors_whenPromoCodeAndLinkedPromotionIdAreMissing() {
        Promotion promotion = Promotion.builder().build();

        List<String> errors = engine.validateUseCaseSpecific(promotion);

        assertEquals(2, errors.size());
        assertTrue(errors.stream().anyMatch(e -> e.contains("promoCode is required")));
        assertTrue(errors.stream().anyMatch(e -> e.contains("linkedPromotionId is required")));
    }

    @Test
    void shouldGetTwoErrors_whenPromoCodeIsBlankAndLinkedPromotionIdMissing() {
        Promotion promotion = Promotion.builder().promoCode("   ").build();

        List<String> errors = engine.validateUseCaseSpecific(promotion);

        assertEquals(2, errors.size());
        assertTrue(errors.stream().anyMatch(e -> e.contains("promoCode is required")));
        assertTrue(errors.stream().anyMatch(e -> e.contains("linkedPromotionId is required")));
    }

    @Test
    void shouldGetError_whenLinkedPromotionIdDoesNotExist() {
        Promotion promotion = Promotion.builder()
                .promoCode("HELLO")
                .linkedPromotionId(INVALID_PROMOTION_ID)
                .build();

        when(promotionRepository.findById(INVALID_PROMOTION_ID)).thenReturn(Optional.empty());

        List<String> errors = engine.validateUseCaseSpecific(promotion);

        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("must be a valid promotion id"));
    }

    @Test
    void shouldGetError_whenPromoCodeAlreadyInUse() {
        Promotion promotion = Promotion.builder()
                .promoCode("HELLO")
                .linkedPromotionId(VALID_PROMOTION_ID)
                .build();

        when(promotionRepository.findByPromoCodeIgnoreCase("HELLO"))
                .thenReturn(Optional.of(new PromotionEntity()));
        when(promotionRepository.findById(VALID_PROMOTION_ID))
                .thenReturn(Optional.of(new PromotionEntity()));

        List<String> errors = engine.validateUseCaseSpecific(promotion);

        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("promoCode HELLO is already in use"));
    }

    @Test
    void noError_whenPromoCodeValid_andLinkedPromotionExists() {
        Promotion promotion = Promotion.builder()
                .promoCode("HELLO")
                .linkedPromotionId(VALID_PROMOTION_ID)
                .build();

        when(promotionRepository.findByPromoCodeIgnoreCase("HELLO"))
                .thenReturn(Optional.empty());
        when(promotionRepository.findById(VALID_PROMOTION_ID))
                .thenReturn(Optional.of(new PromotionEntity()));

        List<String> errors = engine.validateUseCaseSpecific(promotion);

        assertEquals(0, errors.size());
    }
}
