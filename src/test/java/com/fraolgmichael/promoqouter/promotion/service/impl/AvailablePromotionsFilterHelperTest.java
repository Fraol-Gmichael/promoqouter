package com.fraolgmichael.promoqouter.promotion.service.impl;

import com.fraolgmichael.promoqouter.common.exception.ServiceException;
import com.fraolgmichael.promoqouter.common.service.CustomerSegment;
import com.fraolgmichael.promoqouter.product.service.Category;
import com.fraolgmichael.promoqouter.product.service.Product;
import com.fraolgmichael.promoqouter.promotion.PromotionMapper;
import com.fraolgmichael.promoqouter.promotion.dataaccess.PromotionRepository;
import com.fraolgmichael.promoqouter.promotion.dto.CartRequestDto;
import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AvailablePromotionsFilterHelperTest {

    PromotionRepository promotionRepository;
    PromotionMapper promotionMapper;
    AvailablePromotionsFilterHelper helper;

    @BeforeEach
    void setup() {
        promotionRepository = mock(PromotionRepository.class);
        promotionMapper = mock(PromotionMapper.class);
        helper = new AvailablePromotionsFilterHelper(promotionRepository, promotionMapper);
    }

    Product product(UUID id) {
        return Product.builder()
                .id(id)
                .name("P")
                .category(Category.ELECTRONICS)   // enum
                .price(BigDecimal.TEN)
                .stock(1L)
                .build();
    }

    Promotion promotion(String name, CustomerSegment segment) {
        Promotion p = mock(Promotion.class);
        when(p.getName()).thenReturn(name);
        when(p.getCustomerSegment()).thenReturn(segment);
        when(p.getStatus()).thenReturn(Promotion.Status.ACTIVE);
        return p;
    }

    @Test
    void throws_when_out_of_stock() {
        UUID pid = UUID.randomUUID();
        CartRequestDto request = new CartRequestDto(
                List.of(new CartRequestDto.CartItem(pid, 5L)),
                CustomerSegment.REGULAR,
                List.of()
        );

        Product prod = product(pid);
        Function<List<UUID>, List<Product>> fetcher = ids -> List.of(prod);

        assertThatThrownBy(() -> helper.retrievePromotionsForCart(request, fetcher))
                .isInstanceOf(ServiceException.class);
    }

    @Test
    void filter_promotions_prefers_exact_segment() {
        Promotion p1 = promotion("Deal", CustomerSegment.ALL);
        Promotion p2 = promotion("Deal", CustomerSegment.REGULAR);

        List<Promotion> result = AvailablePromotionsFilterHelper.filterPromotions(
                List.of(p1, p2), CustomerSegment.REGULAR
        );

        assertThat(result).containsExactly(p2);
    }

    @Test
    void filter_promotions_falls_back_to_first() {
        Promotion p1 = promotion("Deal", CustomerSegment.ALL);
        Promotion p2 = promotion("Deal", CustomerSegment.GOLD);

        List<Promotion> result = AvailablePromotionsFilterHelper.filterPromotions(
                List.of(p1, p2), CustomerSegment.REGULAR
        );

        assertThat(result).containsExactly(p1);
    }

    @Test
    void filter_promotions_falls_back_to_all() {

    }
}
