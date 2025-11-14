package com.fraolgmichael.promoqouter.product.dto;

import com.fraolgmichael.promoqouter.product.service.Category;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record UpdateProductRequestDto(
        String name,
        @Positive(message = "stock must be positive") Long stock,
        @Positive(message = "price must be positive") BigDecimal price,
        Category category
) {
}
