package com.fraolgmichael.promoqouter.promotion.service.impl;

import com.fraolgmichael.promoqouter.common.exception.ResponseCodes;
import com.fraolgmichael.promoqouter.common.exception.ServiceException;
import com.fraolgmichael.promoqouter.common.service.CustomerSegment;
import com.fraolgmichael.promoqouter.promotion.PromotionMapper;
import com.fraolgmichael.promoqouter.promotion.dataaccess.PromotionEntity;
import com.fraolgmichael.promoqouter.promotion.dataaccess.PromotionRepository;
import com.fraolgmichael.promoqouter.promotion.dto.CreatePromotionRequestDto;
import com.fraolgmichael.promoqouter.promotion.dto.UpdatePromotionRequestDto;
import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import com.fraolgmichael.promoqouter.promotion.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionEngineFactory promotionEngineFactory;
    private final PromotionMapper promotionMapper;
    private final PromotionRepository promotionRepository;

    @Override
    public Promotion create(CreatePromotionRequestDto createPromotionRequestDto) {
        Promotion promotion = promotionMapper.fromDtoToPromotion(createPromotionRequestDto);
        getEngineOrThrow(promotion.getType()).validate(promotion);
        validatePromotionAlreadyExists(createPromotionRequestDto.name(), createPromotionRequestDto.customerSegment());
        PromotionEntity savedEntity = promotionRepository.save(promotionMapper.fromPromotionToEntity(promotion));
        return promotionMapper.fromEntityToPromotion(savedEntity);
    }

    private void validatePromotionAlreadyExists(String name, CustomerSegment customerSegment) {
        if (promotionRepository.findByNameIgnoreCaseAndCustomerSegment(name, customerSegment).isPresent()) {
            throw new ServiceException(
                    ResponseCodes.ALREADY_EXISTS,
                    "Promotion with name " + name + " and customer segment " + customerSegment + " already exists"
            );
        }
    }

    private PromotionEngine getEngineOrThrow(Promotion.Type type) {
        return promotionEngineFactory.getPromotionEngine(type)
                .orElseThrow(
                        () -> new ServiceException(
                                ResponseCodes.NOT_IMPLEMENTED,
                                "Promotion type " + type + " not implemented"
                        )
                );
    }

    @Override
    public Promotion update(UUID id, UpdatePromotionRequestDto updatePromotionRequestDto) {
        Promotion promotion = getPromotionOrElseThrow(id);
        promotionMapper.updatePromotionFromDto(updatePromotionRequestDto, promotion);
        getEngineOrThrow(promotion.getType()).validate(promotion);
        PromotionEntity savedEntity = promotionRepository.save(promotionMapper.fromPromotionToEntity(promotion));
        return promotionMapper.fromEntityToPromotion(savedEntity);
    }

    private Promotion getPromotionOrElseThrow(UUID id) {
        PromotionEntity promotionEntity = promotionRepository.findById(id).orElseThrow(
                () -> new ServiceException(ResponseCodes.NOT_FOUND, "Promotion with id " + id + " not found")
        );
        return promotionMapper.fromEntityToPromotion(promotionEntity);
    }

    @Override
    public Promotion get(UUID id) {
        return getPromotionOrElseThrow(id);
    }

    @Override
    public Long delete(UUID id) {
        return promotionRepository.removeById(id);
    }

    @Override
    public List<Promotion> findByName(String name) {
        return promotionRepository
                .findByNameIgnoreCase(name).stream()
                .map(promotionMapper::fromEntityToPromotion).toList();
    }

    @Override
    public List<Promotion> findAll() {
        return promotionRepository
                .findAll().stream()
                .map(promotionMapper::fromEntityToPromotion).toList();
    }
}
