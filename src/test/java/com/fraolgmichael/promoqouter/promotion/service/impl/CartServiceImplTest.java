package com.fraolgmichael.promoqouter.promotion.service.impl;

import com.fraolgmichael.promoqouter.product.ProductMapper;
import com.fraolgmichael.promoqouter.product.ProductMapperImpl;
import com.fraolgmichael.promoqouter.product.dataaccess.ProductEntity;
import com.fraolgmichael.promoqouter.product.dataaccess.ProductRepository;
import com.fraolgmichael.promoqouter.product.service.Product;
import com.fraolgmichael.promoqouter.product.service.ProductService;
import com.fraolgmichael.promoqouter.promotion.dto.Cart;
import com.fraolgmichael.promoqouter.promotion.dto.CartRequestDto;
import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import com.fraolgmichael.promoqouter.promotion.service.PromotionEngine;
import com.fraolgmichael.promoqouter.promotion.service.PromotionEngineFactory;
import com.fraolgmichael.promoqouter.promotion.service.PromotionsWithProducts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private AvailablePromotionsFilterHelper availablePromotionsFilterHelper;

    @Mock
    private PromotionEngineFactory promotionEngineFactory;

    @Mock
    private PromotionEngine promotionEngine;

    @Spy
    private ProductMapper productMapper = new ProductMapperImpl();

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductService productService;

    @Test
    void quote_appliesPromotionsCorrectly() {

        Product p1 = Product.builder().id(UUID.randomUUID()).price(BigDecimal.TEN).name("A").build();
        Product p2 = Product.builder().id(UUID.randomUUID()).price(BigDecimal.valueOf(100)).name("B").build();
        List<Product> products = List.of(p1, p2);

        CartRequestDto request = CartRequestDto.builder()
                .items(
                        List.of(CartRequestDto.CartItem.builder().productId(p1.getId()).qty(2L).build(),
                                CartRequestDto.CartItem.builder().productId(p2.getId()).qty(2L).build())
                )
                .build();

        Promotion promo = Promotion.builder()
                .id(UUID.randomUUID())
                .type(Promotion.Type.FIXED_DISCOUNT)
                .build();

        PromotionsWithProducts result = new PromotionsWithProducts(
                List.of(promo),
                products
        );

        when(availablePromotionsFilterHelper.retrievePromotionsForCart(eq(request), any()))
                .thenReturn(result);

        when(promotionEngineFactory.getPromotionEngine(promo.getType()))
                .thenReturn(Optional.of(promotionEngine));

        Cart modifiedCart = Cart.builder()
                .productDiscountInfos(Map.of(
                        p1.getId(),
                        Cart.ProductDiscountInfo.builder()
                                .id(p1.getId())
                                .name(p1.getName())
                                .product(p1)
                                .discounts(List.of(Cart.DiscountInfo.builder().appliedDiscount(BigDecimal.TEN).build()))
                                .build()
                ))
                .build();

        when(promotionEngine.apply(eq(promo), eq(products), eq(request), any()))
                .thenReturn(modifiedCart);

        Cart response = cartService.quote(request);

        assertThat(response).isNotNull();
        assertThat(response.getProductDiscountInfos()).hasSize(1);
        assertThat(response.getProductDiscountInfos().get(p1.getId()).getDiscounts().getFirst().getAppliedDiscount()).isEqualTo(BigDecimal.TEN);

        verify(availablePromotionsFilterHelper).retrievePromotionsForCart(eq(request), any());
        verify(promotionEngineFactory).getPromotionEngine(promo.getType());
        verify(promotionEngine).apply(eq(promo), eq(products), eq(request), any());
    }

    @Test
    void confirm_updatesStockAndAppliesPromotions() {
        // Arrange
        Product p1 = Product.builder().id(UUID.randomUUID()).price(BigDecimal.TEN).name("A").stock(10L).build();
        Product p2 = Product.builder().id(UUID.randomUUID()).price(BigDecimal.valueOf(100)).name("B").stock(20L).build();
        List<Product> products = List.of(p1, p2);

        CartRequestDto.CartItem item1 = CartRequestDto.CartItem.builder().productId(p1.getId()).qty(2L).build();
        CartRequestDto.CartItem item2 = CartRequestDto.CartItem.builder().productId(p2.getId()).qty(3L).build();
        CartRequestDto request = CartRequestDto.builder()
                .items(List.of(item1, item2))
                .build();

        Promotion promo = Promotion.builder().id(UUID.randomUUID()).type(Promotion.Type.FIXED_DISCOUNT).build();
        PromotionsWithProducts promotionsWithProducts = new PromotionsWithProducts(List.of(promo), products);

        // Stub helper
        when(availablePromotionsFilterHelper.retrievePromotionsForCart(eq(request), any()))
                .thenReturn(promotionsWithProducts);

        // Stub promotion engine
        when(promotionEngineFactory.getPromotionEngine(promo.getType())).thenReturn(Optional.of(promotionEngine));

        Cart appliedCart = cartService.initialCartResponseDtoFromRequestDto(products, request.items());
        // add dummy discount to first product
        appliedCart.getProductDiscountInfos().get(p1.getId()).getDiscounts()
                .add(Cart.DiscountInfo.builder().appliedDiscount(BigDecimal.TEN).build());

        when(promotionEngine.apply(eq(promo), eq(products), eq(request), any()))
                .thenReturn(appliedCart);

        // Spy mapper so we can verify entity conversion
        var productEntity1 = mock(ProductEntity.class);
        var productEntity2 = mock(ProductEntity.class);
        when(productMapper.fromProductToEntity(p1)).thenReturn(productEntity1);
        when(productMapper.fromProductToEntity(p2)).thenReturn(productEntity2);

        // Act
        Cart result = cartService.confirm(request);

        // Assert cart
        assertThat(result).isNotNull();
        assertThat(result.getProductDiscountInfos().get(p1.getId()).getCartItem().qty()).isEqualTo(2);
        assertThat(result.getProductDiscountInfos().get(p2.getId()).getCartItem().qty()).isEqualTo(3);

        // Verify stock updated
        assertThat(p1.getStock()).isEqualTo(8); // 10 - 2
        assertThat(p2.getStock()).isEqualTo(17); // 20 - 3

        // Verify repository saveAll
        verify(productRepository).saveAll(anyList());

        // Verify promotion engine called
        verify(promotionEngine).apply(eq(promo), eq(products), eq(request), any());
    }

}
