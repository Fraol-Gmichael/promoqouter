package com.fraolgmichael.promoqouter.promotion.service;

import com.fraolgmichael.promoqouter.common.service.CustomerSegment;
import com.fraolgmichael.promoqouter.product.service.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {

    private UUID id;
    private String name;
    private String description;
    private Status status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer priority;
    private BigDecimal budget;
    private Integer maxUsagePerUser;

    private Type type;
    private BigDecimal discountAmount;
    private BigDecimal percentOffAmount;

    private Target target;
    private UUID targetProductId;
    private Category targetCategory;
    @Builder.Default
    private CustomerSegment customerSegment = CustomerSegment.ALL;

    private Integer buyXAmount;
    private Integer getYAmount;
    private BigDecimal pointsToReward;
    private String promoCode;
    private UUID linkedPromotionId;

    public enum Type {
        FIXED_DISCOUNT,
        BUY_X_GET_Y,
        FREE_SHIPPING,
        POINTS_REWARD,
        PERCENT_OFF,
        PROMO_CODE,
    }

    public enum Status {
        ACTIVE,
        INACTIVE
    }

    public enum Target {
        PRODUCT,
        CATEGORY,
    }
}
