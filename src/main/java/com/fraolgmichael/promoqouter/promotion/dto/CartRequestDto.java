package com.fraolgmichael.promoqouter.promotion.dto;

import com.fraolgmichael.promoqouter.common.service.CustomerSegment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
public record CartRequestDto(@NotEmpty(message = "at least one item is required") List<@Valid CartItem> items,
                             CustomerSegment customerSegment,
                             List<@Valid @NotEmpty(message = "promoCode is required") String> promoCodes) {

    public record CartItem(
            @NotNull(message = "productId is required") UUID productId,
            @NotNull(message = "qty is required") @Positive(message = "qty must be positive number") Long qty
    ) {
    }
}
