package com.fraolgmichael.promoqouter.promotion.service.impl;

import com.fraolgmichael.promoqouter.promotion.service.Promotion;

public abstract class PromotionEngine {
    abstract Promotion.Type promotionType();

    public boolean match(Promotion.Type type) {
        return type == promotionType();
    }

    abstract void validate(Promotion promotion);
}
