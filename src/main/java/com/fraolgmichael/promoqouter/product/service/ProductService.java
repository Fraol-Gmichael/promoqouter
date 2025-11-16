package com.fraolgmichael.promoqouter.product.service;

import com.fraolgmichael.promoqouter.product.dto.CreateProductRequestDto;
import com.fraolgmichael.promoqouter.product.dto.UpdateProductRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    Product createProduct(@Valid CreateProductRequestDto createProduct);

    Product updateProduct(UUID id, @Valid UpdateProductRequestDto updateProductRequestDto);

    Product getProduct(@NotNull UUID id);

    List<Product> getProducts(List<UUID> productIds);

    List<Product> getProductsForUpdate(List<UUID> productIds);

    Long deleteProduct(@NotNull UUID id);

    List<Product> findAll();
}
