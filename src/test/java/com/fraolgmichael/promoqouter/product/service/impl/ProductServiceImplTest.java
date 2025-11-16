package com.fraolgmichael.promoqouter.product.service.impl;

import com.fraolgmichael.promoqouter.common.dataaccess.BaseEntity;
import com.fraolgmichael.promoqouter.common.exception.ServiceException;
import com.fraolgmichael.promoqouter.product.ProductMapper;
import com.fraolgmichael.promoqouter.product.ProductMapperImpl;
import com.fraolgmichael.promoqouter.product.dataaccess.ProductEntity;
import com.fraolgmichael.promoqouter.product.dataaccess.ProductRepository;
import com.fraolgmichael.promoqouter.product.dto.CreateProductRequestDto;
import com.fraolgmichael.promoqouter.product.dto.UpdateProductRequestDto;
import com.fraolgmichael.promoqouter.product.service.Category;
import com.fraolgmichael.promoqouter.product.service.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Spy
    private ProductMapper productMapper = new ProductMapperImpl();

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void shouldCreateProductSuccessfully() {
        CreateProductRequestDto req =
                CreateProductRequestDto.builder().name("Test").stock(20L).price(BigDecimal.TEN).build();

        ProductEntity entity = productMapper.fromDtoToProductEntity(req);

        when(productRepository.save(any())).thenReturn(entity);

        Product result = productService.createProduct(req);

        assertEquals("Test", result.getName());
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        UUID id = UUID.randomUUID();

        ProductEntity existing = ProductEntity.builder()
                .id(id)
                .name("Old")
                .category(Category.BEVERAGES)
                .price(BigDecimal.ONE)
                .stock(1L)
                .build();

        UpdateProductRequestDto req =
                UpdateProductRequestDto.builder().name("New").stock(20L).price(BigDecimal.TEN).build();

        when(productRepository.findByIdForUpdate(id)).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);

        Product updated = productService.updateProduct(id, req);

        assertEquals("New", updated.getName());
        assertEquals(Category.BEVERAGES, updated.getCategory());
        verify(productRepository).save(existing);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingProduct() {
        UUID id = UUID.randomUUID();

        assertThrows(ServiceException.class, () -> productService.updateProduct(id, UpdateProductRequestDto.builder().build()));
    }

    @Test
    void shouldThrowExceptionWhenGettingNonExistingProduct() {
        UUID id = UUID.randomUUID();

        assertThrows(ServiceException.class, () -> productService.getProduct(id));
    }

    @Test
    void shouldReturnProductById() {
        UUID id = UUID.randomUUID();

        ProductEntity entity = ProductEntity.builder()
                .id(id)
                .name("Test")
                .category(Category.BEVERAGES)
                .price(BigDecimal.TEN)
                .stock(10L)
                .build();

        when(productRepository.findById(id)).thenReturn(Optional.of(entity));

        Product result = productService.getProduct(id);

        assertEquals(id, result.getId());
        assertEquals("Test", result.getName());
    }

    @Test
    void shouldDeleteProduct() {
        UUID id = UUID.randomUUID();

        when(productRepository.removeById(id)).thenReturn(1L);

        Long result = productService.deleteProduct(id);

        assertEquals(1L, result);
        verify(productRepository).removeById(id);
    }

    @Test
    void shouldReturnAllProducts() {
        List<ProductEntity> entities = List.of(
                ProductEntity.builder().id(UUID.randomUUID()).name("A").category(Category.BEVERAGES).build(),
                ProductEntity.builder().id(UUID.randomUUID()).name("B").category(Category.ELECTRONICS).build()
        );

        when(productRepository.findAll()).thenReturn(entities);

        List<Product> result = productService.findAll();

        assertEquals(2, result.size());
        verify(productRepository).findAll();
    }

    @Test
    void shouldReturnProductsByIds() {
        List<ProductEntity> entities = List.of(
                ProductEntity.builder().id(UUID.randomUUID()).name("A").category(Category.BEVERAGES).build(),
                ProductEntity.builder().id(UUID.randomUUID()).name("B").category(Category.ELECTRONICS).build()
        );
        List<UUID> ids = entities.stream().map(BaseEntity::getId).toList();

        when(productRepository.findByIdIn(ids)).thenReturn(entities);

        List<Product> result = productService.getProducts(ids);

        assertEquals(2, result.size());
        verify(productRepository).findByIdIn(ids);
    }
}
