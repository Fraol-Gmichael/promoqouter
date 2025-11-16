package com.fraolgmichael.promoqouter.promotion.service.impl;

import com.fraolgmichael.promoqouter.product.service.ProductService;
import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import com.fraolgmichael.promoqouter.promotion.service.PromotionEngine;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PromotionEngineFixedDiscountImpl extends PromotionEngine {

    protected PromotionEngineFixedDiscountImpl(ProductService productService) {
        super(productService);
    }

    @Override
    public Promotion.Type promotionType() {
        return Promotion.Type.FIXED_DISCOUNT;
    }

    @Override
    public List<String> validateUseCaseSpecific(Promotion promotion) {
        List<String> errors = new ArrayList<>();

        if (promotion.getDiscountAmount() == null) {
            errors.add("discountAmount is required for promotion type " + promotionType());
        }

        return errors;
    }
}
