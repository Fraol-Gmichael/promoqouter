package com.fraolgmichael.promoqouter.product.controller;

import com.fraolgmichael.promoqouter.common.dto.ApiResponse;
import com.fraolgmichael.promoqouter.product.dto.CreateProductRequestDto;
import com.fraolgmichael.promoqouter.product.dto.UpdateProductRequestDto;
import com.fraolgmichael.promoqouter.product.service.Product;
import com.fraolgmichael.promoqouter.product.service.ProductService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Product> create(@NotNull @RequestBody CreateProductRequestDto createProductRequestDto) {
        return ApiResponse.created(productService.createProduct(createProductRequestDto));
    }

    @PutMapping("/{id}")
    public ApiResponse<Product> update(
            @NotNull @PathVariable UUID id,
            @NotNull @RequestBody UpdateProductRequestDto updateProductRequestDto) {
        return ApiResponse.ok(productService.updateProduct(id, updateProductRequestDto));
    }

    @GetMapping("/{id}")
    public ApiResponse<Product> get(@NotNull @PathVariable UUID id) {
        return ApiResponse.ok(productService.getProduct(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Long> delete(@NotNull @PathVariable UUID id) {
        return ApiResponse.ok(productService.deleteProduct(id));
    }

    @GetMapping
    public ApiResponse<List<Product>> findAll() {
        return ApiResponse.ok(productService.findAll());
    }
}
