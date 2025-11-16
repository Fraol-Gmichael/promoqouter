package com.fraolgmichael.promoqouter.promotion.service.impl;

import com.fraolgmichael.promoqouter.product.service.Product;
import com.fraolgmichael.promoqouter.product.service.ProductService;
import com.fraolgmichael.promoqouter.promotion.dto.Cart;
import com.fraolgmichael.promoqouter.promotion.dto.CartRequestDto;
import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import com.fraolgmichael.promoqouter.promotion.service.PromotionEngine;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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

    @Override
    public Cart apply(Promotion promotion, List<Product> products, CartRequestDto cartRequestDto, Cart cart) {
        if (cart.getTotalPrice().equals(BigDecimal.ZERO)) return cart;
        validate(promotion);
        List<Product> targetProducts = getTargetProducts(promotion, products);
        for (Product targetProduct : targetProducts) {
            Cart.ProductDiscountInfo discountInfo = cart.getProductDiscountInfos().get(targetProduct.getId());
            BigDecimal discountedTotal =
                    calculateDiscountedTotal(promotion, discountInfo.getProduct(), discountInfo.getCartItem());
            updateCartWithProductDiscount(promotion, cart, discountedTotal, discountInfo);
        }
        return cart;
    }

    protected BigDecimal calculateDiscountedTotal(
            Promotion promotion,
            Product product,
            CartRequestDto.CartItem cartItem
    ) {
        long qty = cartItem.qty();
        long buyX = promotion.getBuyXAmount();
        long getY = promotion.getGetYAmount();

        long eligibleFreeItems = (qty / buyX) * getY;
        long payableQty = qty - eligibleFreeItems;
        if (payableQty <= 0) return BigDecimal.ZERO;
        BigDecimal price = product.getPrice();
        return price.multiply(BigDecimal.valueOf(payableQty));
    }

}
