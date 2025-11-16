package com.fraolgmichael.promoqouter.promotion.service.impl;

import com.fraolgmichael.promoqouter.product.service.ProductService;
import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import com.fraolgmichael.promoqouter.promotion.service.PromotionEngine;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class PromotionEngineTieredBulkDiscountImpl extends PromotionEngine {

    protected PromotionEngineTieredBulkDiscountImpl(ProductService productService) {
        super(productService);
    }

    private static void validateTieredInfo(Promotion.TieredInfo tier, List<String> errors) {
        if (tier.getFromValue() == null || tier.getToValue() == null) {
            errors.add("fromValue and toValue must not be null");
            return;
        }
        if (tier.getFromValue() < 0 || tier.getToValue() < 0) {
            errors.add(String.format("fromValue (%d) and toValue (%d) must be >= 0",
                    tier.getFromValue(), tier.getToValue()));
        }
        if (tier.getFromValue() > tier.getToValue()) {
            errors.add(String.format("fromValue (%d) cannot be greater than toValue (%d)",
                    tier.getFromValue(), tier.getToValue()));
        }

    }

    @Override
    public Promotion.Type promotionType() {
        return Promotion.Type.TIERED_BULK_DISCOUNT;
    }

    @Override
    public List<String> validateTarget(Promotion promotion) {
        return new ArrayList<>();
    }

    @Override
    public List<String> validateUseCaseSpecific(Promotion promotion) {
        List<String> errors = new ArrayList<>();

        List<Promotion.TieredInfo> tiers =
                promotion.getTieredInfos() == null ? new ArrayList<>() : new ArrayList<>(promotion.getTieredInfos());

        if (tiers.isEmpty()) {
            errors.add("tieredInfos is required for promotion type " + promotionType());
            return errors;
        }

        for (Promotion.TieredInfo tier : tiers) {
            validateTieredInfo(tier, errors);
        }

        if (!errors.isEmpty()) return errors;

        tiers.sort(Comparator.comparingInt(Promotion.TieredInfo::getFromValue));

        for (int i = 0; i < tiers.size() - 1; i++) {
            Promotion.TieredInfo current = tiers.get(i);
            Promotion.TieredInfo next = tiers.get(i + 1);

            if (current.getToValue() >= next.getFromValue()) {
                errors.add(String.format("Tier [%d-%d] overlaps with [%d-%d]",
                        current.getFromValue(), current.getToValue(),
                        next.getFromValue(), next.getToValue()));
            }
        }

        return errors;
    }
}
