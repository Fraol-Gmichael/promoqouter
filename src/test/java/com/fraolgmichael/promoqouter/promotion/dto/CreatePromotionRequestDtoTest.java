package com.fraolgmichael.promoqouter.promotion.dto;

import com.fraolgmichael.promoqouter.common.service.CustomerSegment;
import org.junit.jupiter.api.Test;

import static com.fraolgmichael.promoqouter.common.service.CustomerSegment.GOLD;
import static com.fraolgmichael.promoqouter.promotion.service.Promotion.Status.ACTIVE;
import static com.fraolgmichael.promoqouter.promotion.service.Promotion.Status.INACTIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreatePromotionRequestDtoTest {

    @Test
    void testDefaultValues() {
        CreatePromotionRequestDto dto = CreatePromotionRequestDto.builder().build();
        assertEquals(ACTIVE, dto.status());
        assertEquals(CustomerSegment.ALL, dto.customerSegment());
    }

    @Test
    void noOverrideValues() {
        CreatePromotionRequestDto dto = CreatePromotionRequestDto.builder().customerSegment(GOLD).status(INACTIVE).build();
        assertEquals(INACTIVE, dto.status());
        assertEquals(GOLD, dto.customerSegment());
    }

}