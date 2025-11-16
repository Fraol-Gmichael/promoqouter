package com.fraolgmichael.promoqouter.promotion.service;

import com.fraolgmichael.promoqouter.common.exception.ResponseCodes;
import com.fraolgmichael.promoqouter.common.exception.ServiceException;
import com.fraolgmichael.promoqouter.product.service.Category;
import com.fraolgmichael.promoqouter.product.service.Product;
import com.fraolgmichael.promoqouter.product.service.ProductService;
import com.fraolgmichael.promoqouter.promotion.dto.Cart;
import com.fraolgmichael.promoqouter.promotion.dto.CartRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Test
    void apply_shouldReturnSameCart() {
        Promotion promotion = mock(Promotion.class);
        List<Product> products = List.of();
        CartRequestDto cartRequestDto = mock(CartRequestDto.class);
        Cart cart = mock(Cart.class);

        Cart result = promotionEngine.apply(promotion, products, cartRequestDto, cart);

        assertSame(cart, result);
    }

    @Test
    void updateCartWithProductDiscount_shouldAddDiscountAndUpdateTotal() {
        // GIVEN
        Product product = mock(Product.class);
        UUID productId = UUID.randomUUID();
        when(product.getId()).thenReturn(productId);
        when(product.getPrice()).thenReturn(new BigDecimal("100"));

        CartRequestDto.CartItem cartItem = mock(CartRequestDto.CartItem.class);
        when(cartItem.qty()).thenReturn(3L); // total price = 300

        Cart.ProductDiscountInfo productDiscountInfo = Cart.ProductDiscountInfo.builder()
                .product(product)
                .cartItem(cartItem)
                .discounts(new ArrayList<>())
                .build();

        Cart cart = Cart.builder().build();
        cart.setProductDiscountInfos(new java.util.HashMap<>());
        cart.getProductDiscountInfos().put(productId, productDiscountInfo);
        cart.setTotalPrice(new BigDecimal("300"));

        Promotion promotion = mock(Promotion.class);
        when(promotion.getName()).thenReturn("Promo Name");
        when(promotion.getDescription()).thenReturn("Promo Description");

        BigDecimal discountedTotal = new BigDecimal("150");

        // WHEN
        promotionEngine.updateCartWithProductDiscount(
                promotion,
                cart,
                discountedTotal,
                productDiscountInfo
        );

        // THEN
        assertEquals(1, productDiscountInfo.getDiscounts().size());
        assertEquals(new BigDecimal("150"), productDiscountInfo.getDiscounts().getFirst().getAppliedDiscount());
        assertEquals(new BigDecimal("150"), cart.getTotalPrice());
    }

    @Test
    void getTargetProducts_whenCategoryTarget_shouldFilterCorrectly() {
        Promotion promotion = mock(Promotion.class);
        when(promotion.getTarget()).thenReturn(Promotion.Target.CATEGORY);
        when(promotion.getTargetCategory()).thenReturn(Category.BEVERAGES);

        Product p1 = mock(Product.class);
        when(p1.getCategory()).thenReturn(Category.BEVERAGES);

        Product p2 = mock(Product.class);
        when(p2.getCategory()).thenReturn(Category.GROCERIES);

        List<Product> result = promotionEngine.getTargetProducts(promotion, List.of(p1, p2));

        assertEquals(1, result.size());
        assertSame(p1, result.getFirst());
    }


    @Test
    void getTargetProducts_whenProductTarget_shouldFilterCorrectly() {
        Promotion promotion = mock(Promotion.class);
        UUID targetId = UUID.randomUUID();

        when(promotion.getTarget()).thenReturn(Promotion.Target.PRODUCT);
        when(promotion.getTargetProductId()).thenReturn(targetId);

        Product p1 = mock(Product.class);
        Product p2 = mock(Product.class);

        when(p1.getId()).thenReturn(targetId);
        when(p2.getId()).thenReturn(UUID.randomUUID());

        List<Product> result = promotionEngine.getTargetProducts(promotion, List.of(p1, p2));

        assertEquals(1, result.size());
        assertSame(p1, result.getFirst());
    }

    @Test
    void getTargetProducts_whenNoMatch_shouldReturnEmptyList() {
        Promotion promotion = mock(Promotion.class);
        when(promotion.getTarget()).thenReturn(Promotion.Target.PRODUCT);
        when(promotion.getTargetProductId()).thenReturn(UUID.randomUUID());

        Product p = mock(Product.class);
        when(p.getId()).thenReturn(UUID.randomUUID());

        List<Product> result = promotionEngine.getTargetProducts(promotion, List.of(p));

        assertEquals(0, result.size());
    }

    @Test
    void getTargetProducts_whenTargetIsQty_shouldReturnEmptyList() {
        Promotion promotion = mock(Promotion.class);
        when(promotion.getTarget()).thenReturn(Promotion.Target.QTY);
        when(promotion.getTargetProductId()).thenReturn(UUID.randomUUID());

        Product p = mock(Product.class);
        when(p.getId()).thenReturn(UUID.randomUUID());

        List<Product> result = promotionEngine.getTargetProducts(promotion, List.of(p));

        assertEquals(0, result.size());
    }
}
