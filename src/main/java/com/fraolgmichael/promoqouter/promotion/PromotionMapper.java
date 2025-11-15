package com.fraolgmichael.promoqouter.promotion;

import com.fraolgmichael.promoqouter.promotion.dataaccess.PromotionEntity;
import com.fraolgmichael.promoqouter.promotion.dto.CreatePromotionRequestDto;
import com.fraolgmichael.promoqouter.promotion.dto.UpdatePromotionRequestDto;
import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PromotionMapper {
    Promotion fromEntityToPromotion(PromotionEntity promotionEntity);

    PromotionEntity fromPromotionToEntity(Promotion promotion);

    Promotion fromDtoToPromotion(CreatePromotionRequestDto createPromotion);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePromotionFromDto(UpdatePromotionRequestDto updatePromotionRequestDto, @MappingTarget Promotion product);

}
