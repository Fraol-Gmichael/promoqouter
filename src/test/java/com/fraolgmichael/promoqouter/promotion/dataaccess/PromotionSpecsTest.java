package com.fraolgmichael.promoqouter.promotion.dataaccess;

import com.fraolgmichael.promoqouter.common.service.CustomerSegment;
import com.fraolgmichael.promoqouter.product.service.Category;
import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PromotionSpecsTest {

    @Autowired
    private PromotionRepository promotionRepository;

    private UUID productId1;

    @BeforeEach
    void setUp() {
        promotionRepository.deleteAll();

        productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();

        PromotionEntity promotion1 = PromotionEntity.builder()
                .name("Product Promo")
                .type(Promotion.Type.PROMO_CODE)
                .status(Promotion.Status.ACTIVE)
                .priority(2)
                .target(Promotion.Target.PRODUCT)
                .targetProductId(productId1)
                .customerSegment(CustomerSegment.ALL)
                .promoCode("PROMO1")
                .build();

        PromotionEntity promotion2 = PromotionEntity.builder()
                .name("Category Promo")
                .type(Promotion.Type.PROMO_CODE)
                .status(Promotion.Status.ACTIVE)
                .priority(1)
                .target(Promotion.Target.CATEGORY)
                .targetCategory(Category.ELECTRONICS)
                .customerSegment(CustomerSegment.ALL)
                .promoCode("PROMO2")
                .build();

        PromotionEntity promotion3 = PromotionEntity.builder()
                .name("Qty Promo")
                .type(Promotion.Type.POINTS_REWARD)
                .status(Promotion.Status.ACTIVE)
                .priority(3)
                .target(Promotion.Target.QTY)
                .customerSegment(CustomerSegment.ALL)
                .build();

        PromotionEntity promotion4 = PromotionEntity.builder()
                .name("Inactive Promo")
                .type(Promotion.Type.POINTS_REWARD)
                .status(Promotion.Status.INACTIVE)
                .priority(4)
                .target(Promotion.Target.QTY)
                .customerSegment(CustomerSegment.ALL)
                .build();

        promotionRepository.saveAll(List.of(promotion1, promotion2, promotion3, promotion4));
    }

    @Test
    void testApplicable_withAllFilters() {
        var spec = PromotionSpecs.applicable(
                List.of(productId1),
                List.of(Category.ELECTRONICS),
                List.of(CustomerSegment.ALL),
                List.of(Promotion.Status.ACTIVE),
                List.of("PROMO1")
        );

        List<PromotionEntity> result = promotionRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "priority"));

        assertThat(result).hasSize(3);
        assertThat(result).extracting("name")
                .containsExactly("Category Promo", "Product Promo", "Qty Promo");
    }

    @Test
    void testApplicable_withNullLists() {
        var spec = PromotionSpecs.applicable(
                null,
                null,
                null,
                null,
                null
        );

        List<PromotionEntity> result = promotionRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "priority"));

        assertThat(result).hasSize(2);
        assertThat(result).extracting("name").containsExactly("Qty Promo", "Inactive Promo");
    }

    @Test
    void testApplicable_withEmptyLists() {
        var spec = PromotionSpecs.applicable(
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );

        List<PromotionEntity> result = promotionRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "priority"));

        assertThat(result).hasSize(2);
        assertThat(result).extracting("name").containsExactly("Qty Promo", "Inactive Promo");
    }

    @Test
    void testApplicable_onlyProductFilter() {
        var spec = PromotionSpecs.applicable(
                List.of(productId1),
                null,
                null,
                List.of(Promotion.Status.ACTIVE),
                null
        );

        List<PromotionEntity> result = promotionRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "priority"));

        assertThat(result).hasSize(2); // Product + Qty
        assertThat(result).extracting("name").containsExactly("Product Promo", "Qty Promo");
    }

    @Test
    void testApplicable_onlyCategoryFilter() {
        var spec = PromotionSpecs.applicable(
                null,
                List.of(Category.ELECTRONICS),
                null,
                List.of(Promotion.Status.ACTIVE),
                null
        );

        List<PromotionEntity> result = promotionRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "priority"));

        assertThat(result).hasSize(2);
        assertThat(result).extracting("name")
                .containsExactly("Category Promo", "Qty Promo");
    }

    @Test
    void testApplicable_onlyPromoCodeFilter() {
        var spec = PromotionSpecs.applicable(
                null,
                null,
                null,
                List.of(Promotion.Status.ACTIVE),
                List.of("PROMO1")
        );

        List<PromotionEntity> result = promotionRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "priority"));

        assertThat(result).hasSize(2); // Product + Qty
        assertThat(result).extracting("name")
                .containsExactly("Product Promo", "Qty Promo");
    }
}
