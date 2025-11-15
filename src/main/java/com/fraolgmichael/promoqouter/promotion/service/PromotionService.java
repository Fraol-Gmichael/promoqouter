package com.fraolgmichael.promoqouter.promotion.service;

import com.fraolgmichael.promoqouter.promotion.dto.CreatePromotionRequestDto;
import com.fraolgmichael.promoqouter.promotion.dto.UpdatePromotionRequestDto;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface PromotionService {
    Promotion create(@Valid CreatePromotionRequestDto createPromotionRequestDto);

    Promotion update(UUID id, @Valid UpdatePromotionRequestDto updatePromotionRequestDto);

    Promotion get(UUID id);

    Long delete(UUID id);

    List<Promotion> findByName(String name);

    List<Promotion> findAll();
}
