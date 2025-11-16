package com.fraolgmichael.promoqouter;

import com.fraolgmichael.promoqouter.common.service.CustomerSegment;
import com.fraolgmichael.promoqouter.product.dataaccess.ProductEntity;
import com.fraolgmichael.promoqouter.product.dataaccess.ProductRepository;
import com.fraolgmichael.promoqouter.product.service.Category;
import com.fraolgmichael.promoqouter.promotion.dataaccess.PromotionEntity;
import com.fraolgmichael.promoqouter.promotion.dataaccess.PromotionEntity.TieredInfo;
import com.fraolgmichael.promoqouter.promotion.dataaccess.PromotionRepository;
import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;

    @Override
    public void run(String... args) throws Exception {
        // ==========================
        // Products
        // ==========================
        ProductEntity product1 = ProductEntity.builder()
                .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .name("Ultra Laptop 14")
                .category(Category.ELECTRONICS)
                .price(BigDecimal.valueOf(1299.99))
                .stock(25L)
                .build();

        ProductEntity product2 = ProductEntity.builder()
                .id(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                .name("Noise-Canceling Headphones")
                .category(Category.ELECTRONICS)
                .price(BigDecimal.valueOf(199.99))
                .stock(150L)
                .build();

        ProductEntity product3 = ProductEntity.builder()
                .id(UUID.fromString("33333333-3333-3333-3333-333333333333"))
                .name("Classic Denim Jacket")
                .category(Category.FASHION)
                .price(BigDecimal.valueOf(89.90))
                .stock(80L)
                .build();

        ProductEntity product4 = ProductEntity.builder()
                .id(UUID.fromString("44444444-4444-4444-4444-444444444444"))
                .name("Organic Apples (1kg)")
                .category(Category.GROCERIES)
                .price(BigDecimal.valueOf(3.49))
                .stock(500L)
                .build();

        ProductEntity product5 = ProductEntity.builder()
                .id(UUID.fromString("55555555-5555-5555-5555-555555555555"))
                .name("Sparkling Water (12-pack)")
                .category(Category.BEVERAGES)
                .price(BigDecimal.valueOf(7.99))
                .stock(200L)
                .build();

        ProductEntity product6 = ProductEntity.builder()
                .id(UUID.fromString("66666666-6666-6666-6666-666666666666"))
                .name("Premium Coffee Beans 1kg")
                .category(Category.BEVERAGES)
                .price(BigDecimal.valueOf(19.95))
                .stock(120L)
                .build();

        productRepository.saveAll(List.of(product1, product2, product3, product4, product5, product6));

        // ==========================
        // Promotions
        // ==========================
        PromotionEntity promo1 = PromotionEntity.builder()
                .id(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1"))
                .name("Autumn Fashion -10%")
                .description("Seasonal 10% off on all fashion items")
                .status(Promotion.Status.ACTIVE)
                .startDate(LocalDateTime.of(2025, 9, 1, 0, 0))
                .endDate(LocalDateTime.of(2026, 1, 31, 23, 59))
                .priority(10)
                .type(Promotion.Type.PERCENT_OFF)
                .percentOffAmount(BigDecimal.valueOf(10))
                .target(Promotion.Target.CATEGORY)
                .targetCategory(Category.FASHION)
                .customerSegment(CustomerSegment.ALL)
                .build();

        PromotionEntity promo2 = PromotionEntity.builder()
                .id(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2"))
                .name("Laptop $50 Off")
                .description("Save $50 on Ultra Laptop 14\"")
                .status(Promotion.Status.ACTIVE)
                .startDate(LocalDateTime.of(2025, 11, 1, 0, 0))
                .endDate(LocalDateTime.of(2026, 12, 31, 23, 59))
                .priority(50)
                .type(Promotion.Type.FIXED_DISCOUNT)
                .discountAmount(BigDecimal.valueOf(50))
                .target(Promotion.Target.PRODUCT)
                .targetProductId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .customerSegment(CustomerSegment.ALL)
                .maxUsagePerUser(1)
                .build();

        PromotionEntity promo3 = PromotionEntity.builder()
                .id(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3"))
                .name("Groceries B2G1")
                .description("Buy 2 get 1 free on selected groceries category")
                .status(Promotion.Status.ACTIVE)
                .startDate(LocalDateTime.of(2025, 11, 1, 0, 0))
                .endDate(LocalDateTime.of(2026, 3, 31, 23, 59))
                .priority(30)
                .type(Promotion.Type.BUY_X_GET_Y)
                .target(Promotion.Target.CATEGORY)
                .targetCategory(Category.GROCERIES)
                .buyXAmount(2)
                .getYAmount(1)
                .customerSegment(CustomerSegment.ALL)
                .build();

        PromotionEntity promo4 = PromotionEntity.builder()
                .id(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4"))
                .name("SUMMER20")
                .description("20% off with promo code SUMMER20")
                .status(Promotion.Status.ACTIVE)
                .startDate(LocalDateTime.of(2025, 6, 1, 0, 0))
                .endDate(LocalDateTime.of(2026, 9, 30, 23, 59))
                .priority(5)
                .type(Promotion.Type.PROMO_CODE)
                .percentOffAmount(BigDecimal.valueOf(20))
                .promoCode("SUMMER20")
                .customerSegment(CustomerSegment.ALL)
                .maxUsagePerUser(5)
                .build();

        PromotionEntity promo5 = PromotionEntity.builder()
                .id(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa5"))
                .name("Beverages Bulk Save")
                .description("Buy more beverages and save with tiered discounts")
                .status(Promotion.Status.ACTIVE)
                .startDate(LocalDateTime.of(2025, 11, 1, 0, 0))
                .endDate(LocalDateTime.of(2026, 12, 31, 23, 59))
                .priority(20)
                .type(Promotion.Type.TIERED_BULK_DISCOUNT)
                .target(Promotion.Target.CATEGORY)
                .targetCategory(Category.BEVERAGES)
                .tieredInfos(List.of(
                        new TieredInfo(6, 10, BigDecimal.valueOf(0.05)),
                        new TieredInfo(11, 9999, BigDecimal.valueOf(0.10))
                ))
                .customerSegment(CustomerSegment.ALL)
                .build();

        promotionRepository.saveAll(List.of(promo1, promo2, promo3, promo4, promo5));
    }
}
