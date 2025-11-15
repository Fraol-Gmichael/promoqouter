package com.fraolgmichael.promoqouter.promotion.service;

public abstract class PromotionEngine {
    abstract Promotion.Type promotionType();

    public boolean match(Promotion.Type type) {
        return type == promotionType();
    }

    public abstract void validate(Promotion promotion);
}
