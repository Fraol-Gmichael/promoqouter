package com.fraolgmichael.promoqouter.product.dto;

import com.fraolgmichael.promoqouter.product.service.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record CreateProductRequestDto(
        @NotBlank(message = "name is required") String name,
        @NotNull(message = "name is required") @Positive(message = "stock must be positive") Long stock,
        @NotNull(message = "price is required") @Positive(message = "price must be positive") BigDecimal price,
        @NotNull(message = "category is required") Category category
) {
}
