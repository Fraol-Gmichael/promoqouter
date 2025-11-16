package com.fraolgmichael.promoqouter.promotion.service;

import com.fraolgmichael.promoqouter.common.exception.ResponseCodes;
import com.fraolgmichael.promoqouter.common.exception.ServiceException;
import com.fraolgmichael.promoqouter.product.service.Category;
import com.fraolgmichael.promoqouter.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PromotionEngineTest {

    private ProductService productService;
    private PromotionEngine promotionEngine;

    @BeforeEach
    void setup() {
        productService = Mockito.mock(ProductService.class);
        promotionEngine = new TestPromotionEngine(productService);
    }

    @Test
    void throwIfInvalid_whenNoErrors_shouldNotThrow() {
        assertDoesNotThrow(() -> promotionEngine.throwIfInvalid(List.of()));
        assertDoesNotThrow(() -> promotionEngine.throwIfInvalid(null));
    }

    @Test
    void throwIfInvalid_whenErrorsPresent_shouldThrowServiceException() {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> promotionEngine.throwIfInvalid(List.of("error1"))
        );
        assertEquals(ResponseCodes.BAD_REQUEST, ex.getResponseCode());
        assertEquals("error1", ex.getMessage());
    }

    @Test
    void validateTarget_whenTargetIsMissing_shouldReturnError() {
        Promotion promotion = mock(Promotion.class);
        when(promotion.getTarget()).thenReturn(null);

        List<String> errors = promotionEngine.validateTarget(promotion);

        assertEquals(1, errors.size());
        assertTrue(errors.getFirst().contains("target is required"));
    }

    @Test
    void validateTarget_whenCategoryTargetMissingCategoryField_shouldReturnError() {
        Promotion promotion = mock(Promotion.class);
        when(promotion.getTarget()).thenReturn(Promotion.Target.CATEGORY);
        when(promotion.getTargetCategory()).thenReturn(null);

        List<String> errors = promotionEngine.validateTarget(promotion);

        assertEquals(1, errors.size());
        assertTrue(errors.getFirst().contains("targetCategory is required"));
    }

    @Test
    void validateTarget_whenCategoryTargetExistsCategoryField_shouldNotReturnError() {
        Promotion promotion = mock(Promotion.class);
        when(promotion.getTarget()).thenReturn(Promotion.Target.CATEGORY);
        when(promotion.getTargetCategory()).thenReturn(Category.BEVERAGES);

        List<String> errors = promotionEngine.validateTarget(promotion);

        assertEquals(0, errors.size());
    }

    @Test
    void validateTarget_whenProductTargetMissingProductId_shouldReturnError() {
        Promotion promotion = mock(Promotion.class);
        when(promotion.getTarget()).thenReturn(Promotion.Target.PRODUCT);
        when(promotion.getTargetProductId()).thenReturn(null);

        List<String> errors = promotionEngine.validateTarget(promotion);

        assertEquals(1, errors.size());
        assertTrue(errors.getFirst().contains("targetProductId is required"));
    }

    @Test
    void validateTarget_whenProductTargetHasProductId_shouldCallProductService() {
        Promotion promotion = mock(Promotion.class);
        UUID productId = UUID.randomUUID();

        when(promotion.getTarget()).thenReturn(Promotion.Target.PRODUCT);
        when(promotion.getTargetProductId()).thenReturn(productId);

        promotionEngine.validateTarget(promotion);

        verify(productService).getProduct(productId);
    }

    @Test
    void validateStartAndEnd_shouldThrowWhenStartAfterEnd() {
        Promotion promotion = Promotion.builder().build();
        promotion.setStartDate(LocalDateTime.now());
        promotion.setEndDate(LocalDateTime.now().minusDays(1));

        List<String> errors = promotionEngine.validateStartAndEndDate(promotion);
        assertEquals(1, errors.size());
    }

    @Test
    void validateStartAndEnd_success() {
        Promotion promotion = Promotion.builder().build();
        promotion.setStartDate(LocalDateTime.now());
        promotion.setEndDate(LocalDateTime.now().plusDays(1));

        List<String> errors = promotionEngine.validateStartAndEndDate(promotion);
        assertEquals(0, errors.size());

        promotion.setStartDate(null);
        errors = promotionEngine.validateStartAndEndDate(promotion);
        assertEquals(0, errors.size());

        promotion.setEndDate(null);
        errors = promotionEngine.validateStartAndEndDate(promotion);
        assertEquals(0, errors.size());

        promotion.setEndDate(null);
        promotion.setStartDate(LocalDateTime.now());
        errors = promotionEngine.validateStartAndEndDate(promotion);
        assertEquals(0, errors.size());
    }

    @Test
    void match_shouldReturnTrueForSameType() {
        assertTrue(promotionEngine.match(Promotion.Type.BUY_X_GET_Y));
    }

    @Test
    void match_shouldReturnFalseForDifferentType() {
        assertFalse(promotionEngine.match(Promotion.Type.POINTS_REWARD));
    }

    @Test
    void validate() {
        Promotion promotion = Promotion.builder()
                .target(Promotion.Target.CATEGORY)
                .targetCategory(Category.BEVERAGES)
                .build();
        promotionEngine.validate(promotion);
    }

    @Test
    void validate_fails() {
        Promotion promotion = Promotion.builder()
                .target(Promotion.Target.CATEGORY)
                .build();
        assertThrows(ServiceException.class, () -> promotionEngine.validate(promotion));
    }

    private static class TestPromotionEngine extends PromotionEngine {

        protected TestPromotionEngine(ProductService productService) {
            super(productService);
        }

        @Override
        public Promotion.Type promotionType() {
            return Promotion.Type.BUY_X_GET_Y;
        }

        @Override
        public List<String> validateUseCaseSpecific(Promotion promotion) {
            return List.of();
        }
    }
}
