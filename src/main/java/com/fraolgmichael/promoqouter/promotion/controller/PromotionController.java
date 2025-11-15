package com.fraolgmichael.promoqouter.promotion.controller;


import com.fraolgmichael.promoqouter.common.dto.ApiResponse;
import com.fraolgmichael.promoqouter.promotion.dto.CreatePromotionRequestDto;
import com.fraolgmichael.promoqouter.promotion.dto.UpdatePromotionRequestDto;
import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import com.fraolgmichael.promoqouter.promotion.service.PromotionService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/promotions")
public class PromotionController {
    private final PromotionService promotionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Promotion> create(@NotNull @RequestBody CreatePromotionRequestDto promotion) {
        return ApiResponse.created(promotionService.create(promotion));
    }

    @PutMapping("/{id}")
    public ApiResponse<Promotion> update(
            @NotNull @PathVariable UUID id,
            @NotNull @RequestBody UpdatePromotionRequestDto promotion) {
        return ApiResponse.ok(promotionService.update(id, promotion));
    }

    @GetMapping("/{id}")
    public ApiResponse<Promotion> get(@NotNull @PathVariable UUID id) {
        return ApiResponse.ok(promotionService.get(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Long> delete(@NotNull @PathVariable UUID id) {
        return ApiResponse.ok(promotionService.delete(id));
    }

    @GetMapping
    public ApiResponse<List<Promotion>> findAll() {
        return ApiResponse.ok(promotionService.findAll());
    }

    @GetMapping("/get-by-name/{name}")
    public ApiResponse<List<Promotion>> findByName(
            @NotNull @PathVariable String name
    ) {
        return ApiResponse.ok(promotionService.findByName(name));
    }
}
