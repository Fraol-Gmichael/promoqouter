package com.fraolgmichael.promoqouter.promotion.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Cart(List<ProductDiscountInfo> productDiscountInfos, BigDecimal totalPrice) {

    @Builder(toBuilder = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ProductDiscountInfo(
            String name,
            UUID id,
            List<DiscountInfo> discounts
    ) {
    }

    @Builder(toBuilder = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DiscountInfo(
            String name,
            BigDecimal appliedDiscount,
            String description,
            Promotion.Type type,
            Map<String, Object> additionalInfo
    ) {
    }
}
