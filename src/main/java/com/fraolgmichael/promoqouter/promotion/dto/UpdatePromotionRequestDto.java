package com.fraolgmichael.promoqouter.promotion.dto;

import com.fraolgmichael.promoqouter.common.service.CustomerSegment;
import com.fraolgmichael.promoqouter.product.service.Category;
import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
public record UpdatePromotionRequestDto(
        String name,
        String description,
        Promotion.Status status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer priority,
        @Positive(message = "budget must be positive number") BigDecimal budget,
        @Positive(message = "maxUsagePerUser must be positive number") Integer maxUsagePerUser,

        Promotion.Type type,
        @Positive(message = "discountAmount must be positive number") BigDecimal discountAmount,
        @Positive(message = "percentOffAmount must be positive number") BigDecimal percentOffAmount,

        Promotion.Target target,
        UUID targetProductId,
        Category targetCategory,
        CustomerSegment customerSegment,

        @Positive(message = "buyXAmount must be positive number") Integer buyXAmount,
        @Positive(message = "getYAmount must be positive number") Integer getYAmount,
        @Positive(message = "pointsToReward must be positive number") BigDecimal pointsToReward,
        String promoCode,
        UUID linkedPromotionId,
        List<Promotion.TieredInfo> tieredInfos
) {
}
