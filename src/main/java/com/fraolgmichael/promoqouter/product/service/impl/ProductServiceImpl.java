package com.fraolgmichael.promoqouter.product.service.impl;

import com.fraolgmichael.promoqouter.common.exception.ResponseCodes;
import com.fraolgmichael.promoqouter.common.exception.ServiceException;
import com.fraolgmichael.promoqouter.product.ProductMapper;
import com.fraolgmichael.promoqouter.product.dataaccess.ProductEntity;
import com.fraolgmichael.promoqouter.product.dataaccess.ProductRepository;
import com.fraolgmichael.promoqouter.product.dto.CreateProductRequestDto;
import com.fraolgmichael.promoqouter.product.dto.UpdateProductRequestDto;
import com.fraolgmichael.promoqouter.product.service.Product;
import com.fraolgmichael.promoqouter.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public Product createProduct(CreateProductRequestDto createProduct) {
        ProductEntity savedProduct = productRepository.save(productMapper.fromDtoToProductEntity(createProduct));
        return productMapper.fromEntityToProduct(savedProduct);
    }

    @Override
    @Transactional
    public Product updateProduct(UUID id, UpdateProductRequestDto updateProductRequestDto) {
        ProductEntity product = getProductForUpdateOrElseThrow(id);
        productMapper.updateProductFromDto(updateProductRequestDto, product);
        return productMapper.fromEntityToProduct(productRepository.save(product));

    }

    private ProductEntity getProductForUpdateOrElseThrow(UUID id) {
        return productRepository.findByIdForUpdate(id).orElseThrow(
                () -> new ServiceException(ResponseCodes.NOT_FOUND, "Product with id " + id + " not found")
        );
    }

    @Override
    public Product getProduct(UUID id) {
        ProductEntity productEntity = getProductOrElseThrow(id);
        return productMapper.fromEntityToProduct(productEntity);
    }

    private ProductEntity getProductOrElseThrow(UUID id) {
        return productRepository.findById(id).orElseThrow(
                () -> new ServiceException(ResponseCodes.NOT_FOUND, "Product with id " + id + " not found")
        );
    }

    @Override
    public Long deleteProduct(UUID id) {
        return productRepository.removeById(id);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll().stream()
                .map(productMapper::fromEntityToProduct)
                .toList();
    }
}
