package com.fraolgmichael.promoqouter.promotion.service.impl;

import com.fraolgmichael.promoqouter.product.service.Product;
import com.fraolgmichael.promoqouter.product.service.ProductService;
import com.fraolgmichael.promoqouter.promotion.dto.Cart;
import com.fraolgmichael.promoqouter.promotion.dto.CartRequestDto;
import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import com.fraolgmichael.promoqouter.promotion.service.PromotionEngine;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class PromotionEnginePercentOFFImpl extends PromotionEngine {
    protected PromotionEnginePercentOFFImpl(ProductService productService) {
        super(productService);
    }

    @Override
    public Promotion.Type promotionType() {
        return Promotion.Type.PERCENT_OFF;
    }

    @Override
    public List<String> validateUseCaseSpecific(Promotion promotion) {
        List<String> errors = new ArrayList<>();
        if (promotion.getPercentOffAmount() == null)
            errors.add("percentOffAmount is required for promotion type " + promotionType());

        return errors;
    }

    @Override
    public Cart apply(Promotion promotion, List<Product> products, CartRequestDto cartRequestDto, Cart cart) {
        if (cart.getTotalPrice().equals(BigDecimal.ZERO)) return cart;
        validate(promotion);
        List<Product> targetProducts = getTargetProducts(promotion, products);
        for (Product targetProduct : targetProducts) {
            Cart.ProductDiscountInfo discountInfo = cart.getProductDiscountInfos().get(targetProduct.getId());
            BigDecimal discountedTotal =
                    calculatePercentOff(promotion, discountInfo.getProduct(), discountInfo.getCartItem());
            updateCartWithProductDiscount(promotion, cart, discountedTotal, discountInfo);
        }
        return cart;
    }

    protected BigDecimal calculatePercentOff(
            Promotion promotion,
            Product product,
            CartRequestDto.CartItem cartItem
    ) {
        BigDecimal discountAmount = product.getPrice()
                .multiply(BigDecimal.valueOf(cartItem.qty()))
                .multiply(promotion.getPercentOffAmount())
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
        return product.getPrice().multiply(BigDecimal.valueOf(cartItem.qty())).subtract(discountAmount);
    }
}
