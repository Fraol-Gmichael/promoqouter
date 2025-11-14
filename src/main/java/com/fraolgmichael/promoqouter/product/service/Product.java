package com.fraolgmichael.promoqouter.product.service;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class Product {
    private UUID id;
    private String name;
    private Category category;
    private BigDecimal price;
    private Long stock;
}
