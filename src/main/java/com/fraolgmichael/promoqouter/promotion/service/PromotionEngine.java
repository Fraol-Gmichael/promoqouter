package com.fraolgmichael.promoqouter.promotion.service;

import com.fraolgmichael.promoqouter.common.exception.ResponseCodes;
import com.fraolgmichael.promoqouter.common.exception.ServiceException;
import com.fraolgmichael.promoqouter.product.service.ProductService;

import java.util.ArrayList;
import java.util.List;

public abstract class PromotionEngine {
    protected final ProductService productService;

    protected PromotionEngine(ProductService productService) {
        this.productService = productService;
    }

    public abstract Promotion.Type promotionType();

    protected void throwIfInvalid(List<String> errors) {
        if (errors == null || errors.isEmpty()) return;
        throw new ServiceException(ResponseCodes.BAD_REQUEST, String.join(", ", errors));
    }

    public List<String> validateTarget(Promotion promotion) {
        List<String> errors = new ArrayList<>();
        if (promotion.getTarget() == null) {
            errors.add("target is required for promotion type " + promotionType());
        }
        if (promotion.getTarget() == Promotion.Target.CATEGORY && promotion.getTargetCategory() == null) {
            errors.add("targetCategory is required for promotion type " + promotionType());
        }
        if (promotion.getTarget() == Promotion.Target.PRODUCT && promotion.getTargetProductId() == null) {
            errors.add("targetProductId is required for promotion type " + promotionType());
        }
        if (promotion.getTarget() == Promotion.Target.PRODUCT && promotion.getTargetProductId() != null) {
            productService.getProduct(promotion.getTargetProductId());
        }
        return errors;
    }

    public List<String> validateStartAndEndDate(Promotion promotion) {
        List<String> errors = new ArrayList<>();
        if (promotion.getStartDate() != null && promotion.getEndDate() != null &&
                promotion.getStartDate().isAfter(promotion.getEndDate())) {
            errors.add("startDate must be before endDate");
        }
        return errors;
    }

    public void validate(Promotion promotion) {
        List<String> errors = new ArrayList<>();
        errors.addAll(validateTarget(promotion));
        errors.addAll(validateStartAndEndDate(promotion));
        errors.addAll(validateUseCaseSpecific(promotion));
        throwIfInvalid(errors);
    }

    public boolean match(Promotion.Type type) {
        return type == promotionType();
    }

    public abstract List<String> validateUseCaseSpecific(Promotion promotion);
}
