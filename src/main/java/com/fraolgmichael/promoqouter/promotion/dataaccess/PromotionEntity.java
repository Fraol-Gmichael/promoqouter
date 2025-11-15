package com.fraolgmichael.promoqouter.promotion.dataaccess;

import com.fraolgmichael.promoqouter.common.dataaccess.BaseEntity;
import com.fraolgmichael.promoqouter.common.service.CustomerSegment;
import com.fraolgmichael.promoqouter.product.service.Category;
import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "promoqouter__promotions")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PromotionEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Promotion.Status status;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Integer priority;
    private BigDecimal budget;
    private Integer maxUsagePerUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Promotion.Type type;

    private BigDecimal discountAmount;
    private BigDecimal percentOffAmount;

    @Enumerated(EnumType.STRING)
    private Promotion.Target target;

    private UUID targetProductId;

    @Enumerated(EnumType.STRING)
    private Category targetCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CustomerSegment customerSegment = CustomerSegment.ALL;

    @ElementCollection
    @CollectionTable(
            name = "promotion_free_products",
            joinColumns = @JoinColumn(name = "promotion_id")
    )
    @Column(name = "product_id")
    private List<UUID> freeProductIds;

    private Integer buyXAmount;
    private Integer getYAmount;

    private BigDecimal pointsToReward;

    @ElementCollection
    @CollectionTable(
            name = "promotion_bundle_products",
            joinColumns = @JoinColumn(name = "promotion_id")
    )
    @Column(name = "product_id")
    private List<UUID> bundleProductIds;

    private BigDecimal bundlePriceOverride;

    private String promoCode;

    private UUID linkedPromotionId;
}
