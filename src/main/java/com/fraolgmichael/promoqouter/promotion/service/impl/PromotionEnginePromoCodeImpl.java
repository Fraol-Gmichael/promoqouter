package com.fraolgmichael.promoqouter.promotion.service.impl;

import com.fraolgmichael.promoqouter.product.service.ProductService;
import com.fraolgmichael.promoqouter.promotion.dataaccess.PromotionRepository;
import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import com.fraolgmichael.promoqouter.promotion.service.PromotionEngine;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PromotionEnginePromoCodeImpl extends PromotionEngine {
    private final PromotionRepository promotionRepository;

    protected PromotionEnginePromoCodeImpl(ProductService productService, PromotionRepository promotionRepository) {
        super(productService);
        this.promotionRepository = promotionRepository;
    }

    @Override
    public Promotion.Type promotionType() {
        return Promotion.Type.PROMO_CODE;
    }

    @Override
    public List<String> validateUseCaseSpecific(Promotion promotion) {
        List<String> errors = new ArrayList<>();

        if (promotion.getPromoCode() == null || promotion.getPromoCode().isBlank()) {
            errors.add("promoCode is required for promotion type " + promotionType());
        } else {
            if (promotionRepository.findByPromoCodeIgnoreCase(promotion.getPromoCode().trim()).isPresent()) {
                errors.add("promoCode " + promotion.getPromoCode() + " is already in use");
            }
        }

        if (promotion.getLinkedPromotionId() == null) {
            errors.add("linkedPromotionId is required for promotion type " + promotionType());
        } else {
            if (promotionRepository.findById(promotion.getLinkedPromotionId()).isEmpty()) {
                errors.add("linkedPromotionId " + promotion.getLinkedPromotionId() + " must be a valid promotion id");
            }
        }

        return errors;
    }
}
