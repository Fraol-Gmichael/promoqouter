package com.fraolgmichael.promoqouter.product;

import com.fraolgmichael.promoqouter.product.dataaccess.ProductEntity;
import com.fraolgmichael.promoqouter.product.dto.CreateProductRequestDto;
import com.fraolgmichael.promoqouter.product.dto.UpdateProductRequestDto;
import com.fraolgmichael.promoqouter.product.service.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductEntity fromDtoToProductEntity(CreateProductRequestDto createProduct);

    Product fromEntityToProduct(ProductEntity productEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductFromDto(UpdateProductRequestDto updateProductRequestDto, @MappingTarget ProductEntity product);
}
