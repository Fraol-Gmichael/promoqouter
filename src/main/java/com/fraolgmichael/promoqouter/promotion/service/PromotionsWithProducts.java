package com.fraolgmichael.promoqouter.promotion.service;

import com.fraolgmichael.promoqouter.product.service.Product;
import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record PromotionsWithProducts(List<Promotion> promotions, List<Product> products) {

}