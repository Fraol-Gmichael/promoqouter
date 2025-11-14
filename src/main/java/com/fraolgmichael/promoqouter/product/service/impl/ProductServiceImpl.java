package com.fraolgmichael.promoqouter.product.service.impl;

import com.fraolgmichael.promoqouter.product.dto.CreateProductRequestDto;
import com.fraolgmichael.promoqouter.product.dto.UpdateProductRequestDto;
import com.fraolgmichael.promoqouter.product.service.Product;
import com.fraolgmichael.promoqouter.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    @Override
    public Product createProduct(CreateProductRequestDto createProduct) {
        return null;
    }

    @Override
    public Product updateProduct(UUID id, UpdateProductRequestDto updateProductRequestDto) {
        return null;
    }

    @Override
    public Product getProduct(UUID id) {
        return null;
    }

    @Override
    public Long deleteProduct(UUID id) {
        return 0L;
    }

    @Override
    public List<Product> findAll() {
        return List.of();
    }
}
