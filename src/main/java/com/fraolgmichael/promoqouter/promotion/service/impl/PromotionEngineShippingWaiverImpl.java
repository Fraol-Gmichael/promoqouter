package com.fraolgmichael.promoqouter.promotion.service.impl;

import com.fraolgmichael.promoqouter.product.service.ProductService;
import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import com.fraolgmichael.promoqouter.promotion.service.PromotionEngine;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PromotionEngineShippingWaiverImpl extends PromotionEngine {

    protected PromotionEngineShippingWaiverImpl(ProductService productService) {
        super(productService);
    }

    @Override
    public Promotion.Type promotionType() {
        return Promotion.Type.FREE_SHIPPING;
    }

    @Override
    public List<String> validateUseCaseSpecific(Promotion promotion) {
        return new ArrayList<>();
    }
}
