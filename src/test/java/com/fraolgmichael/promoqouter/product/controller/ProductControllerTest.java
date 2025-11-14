package com.fraolgmichael.promoqouter.product.controller;

import com.fraolgmichael.promoqouter.product.dto.CreateProductRequestDto;
import com.fraolgmichael.promoqouter.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    ProductService productService;

    @InjectMocks
    ProductController productController;

    @Test
    void create() {
        productController.create(CreateProductRequestDto.builder().build());
        Mockito.verify(productService).createProduct(Mockito.any());
    }

    @Test
    void update() {
        productController.update(Mockito.any(), Mockito.any());
        Mockito.verify(productService).updateProduct(Mockito.any(), Mockito.any());
    }

    @Test
    void get() {
        productController.get(Mockito.any());
        Mockito.verify(productService).getProduct(Mockito.any());
    }

    @Test
    void delete() {
        productController.delete(Mockito.any());
        Mockito.verify(productService).deleteProduct(Mockito.any());
    }

    @Test
    void findAll() {
        productController.findAll();
        Mockito.verify(productService).findAll();
    }

}