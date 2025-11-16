package com.fraolgmichael.promoqouter.promotion.service.impl;

import com.fraolgmichael.promoqouter.product.service.ProductService;
import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import com.fraolgmichael.promoqouter.promotion.service.PromotionEngine;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PromotionEngineBuyXGetYImpl extends PromotionEngine {

    protected PromotionEngineBuyXGetYImpl(ProductService productService) {
        super(productService);
    }

    @Override
    public Promotion.Type promotionType() {
        return Promotion.Type.BUY_X_GET_Y;
    }

    @Override
    public List<String> validateUseCaseSpecific(Promotion promotion) {
        List<String> errors = new ArrayList<>();
        if (promotion.getGetYAmount() == null) {
            errors.add("getYAmount is required for promotion type " + promotionType());
        }
        if (promotion.getBuyXAmount() == null) {
            errors.add("buyXAmount is required for promotion type " + promotionType());
        }

        return errors;
    }
}
