package com.fraolgmichael.promoqouter.promotion.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fraolgmichael.promoqouter.product.service.Product;
import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Cart {
    private Map<UUID, ProductDiscountInfo> productDiscountInfos;
    private BigDecimal totalPrice;

    @Builder(toBuilder = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    public static class ProductDiscountInfo {
        private String name;
        private UUID id;
        @JsonIgnore
        private Product product;
        @JsonIgnore
        private CartRequestDto.CartItem cartItem;
        private List<DiscountInfo> discounts;
    }

    @Builder(toBuilder = true)
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DiscountInfo {
        private String name;
        private BigDecimal appliedDiscount;
        private String description;
        private Promotion.Type type;
        private Map<String, Object> additionalInfo;
    }
}
