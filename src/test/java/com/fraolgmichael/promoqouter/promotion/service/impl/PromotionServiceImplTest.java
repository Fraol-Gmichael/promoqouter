package com.fraolgmichael.promoqouter.promotion.service.impl;

import com.fraolgmichael.promoqouter.common.exception.ResponseCodes;
import com.fraolgmichael.promoqouter.common.exception.ServiceException;
import com.fraolgmichael.promoqouter.promotion.PromotionMapper;
import com.fraolgmichael.promoqouter.promotion.PromotionMapperImpl;
import com.fraolgmichael.promoqouter.promotion.dataaccess.PromotionEntity;
import com.fraolgmichael.promoqouter.promotion.dataaccess.PromotionRepository;
import com.fraolgmichael.promoqouter.promotion.dto.CreatePromotionRequestDto;
import com.fraolgmichael.promoqouter.promotion.dto.UpdatePromotionRequestDto;
import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromotionServiceImplTest {

    @Mock
    private PromotionRepository promotionRepository;
    @Mock
    private PromotionEngine promotionEngine;
    @Mock
    private PromotionEngineFactory promotionEngineFactory;
    @Spy
    private PromotionMapper promotionMapper = new PromotionMapperImpl();

    @InjectMocks
    private PromotionServiceImpl promotionService;

    @Test
    void create_whenPromotionTypeNotSupported_shouldThrowServiceException() {
        ServiceException exception = assertThrows(ServiceException.class, () ->
                promotionService.create(CreatePromotionRequestDto.builder()
                        .type(Promotion.Type.BUY_X_GET_Y)
                        .build())
        );
        assertEquals(ResponseCodes.NOT_IMPLEMENTED, exception.getResponseCode());
    }

    @Test
    void create_whenPromotionAlreadyExists_shouldThrowServiceException() {
        when(promotionEngineFactory.getPromotionEngine(Promotion.Type.BUY_X_GET_Y))
                .thenReturn(Optional.of(promotionEngine));
        when(promotionRepository.findByNameIgnoreCaseAndCustomerSegment(any(), any()))
                .thenReturn(Optional.of(new PromotionEntity()));

        ServiceException exception = assertThrows(ServiceException.class, () ->
                promotionService.create(CreatePromotionRequestDto.builder()
                        .type(Promotion.Type.BUY_X_GET_Y)
                        .build())
        );
        assertEquals(ResponseCodes.ALREADY_EXISTS, exception.getResponseCode());
    }

    @Test
    void create() {
        when(promotionEngineFactory.getPromotionEngine(Promotion.Type.BUY_X_GET_Y))
                .thenReturn(Optional.of(promotionEngine));
        when(promotionRepository.save(any())).thenReturn(new PromotionEntity());
        when(promotionRepository.findByNameIgnoreCaseAndCustomerSegment(any(), any()))
                .thenReturn(Optional.empty());

        promotionService.create(CreatePromotionRequestDto.builder()
                .type(Promotion.Type.BUY_X_GET_Y)
                .build());

        verify(promotionEngine).validate(any());
        verify(promotionRepository).save(any());
    }

    @Test
    void update() {
        UUID id = UUID.randomUUID();
        PromotionEntity entity = new PromotionEntity();

        when(promotionRepository.findById(id)).thenReturn(Optional.of(entity));
        when(promotionEngineFactory.getPromotionEngine(any())).thenReturn(Optional.of(promotionEngine));
        when(promotionRepository.save(any())).thenReturn(entity);

        promotionService.update(id, UpdatePromotionRequestDto.builder().build());

        verify(promotionEngine).validate(any());
        verify(promotionRepository).save(any());
    }

    @Test
    void get() {
        UUID id = UUID.randomUUID();
        PromotionEntity entity = new PromotionEntity();

        when(promotionRepository.findById(id)).thenReturn(Optional.of(entity));

        promotionService.get(id);

        verify(promotionRepository).findById(id);
    }

    @Test
    void get_shouldThrowExceptionWhenPromotionNotFound() {
        UUID id = UUID.randomUUID();

        ServiceException serviceException = assertThrows(ServiceException.class, () -> promotionService.get(id));
        assertEquals(ResponseCodes.NOT_FOUND, serviceException.getResponseCode());
    }

    @Test
    void delete() {
        UUID id = UUID.randomUUID();
        when(promotionRepository.removeById(id)).thenReturn(1L);

        Long result = promotionService.delete(id);

        assertEquals(1L, result);
        verify(promotionRepository).removeById(id);
    }

    @Test
    void findByName() {
        when(promotionRepository.findByNameIgnoreCase("promo"))
                .thenReturn(List.of(new PromotionEntity()));

        List<Promotion> result = promotionService.findByName("promo");

        assertEquals(1, result.size());
        verify(promotionRepository).findByNameIgnoreCase("promo");
    }

    @Test
    void findAll() {
        when(promotionRepository.findAll())
                .thenReturn(List.of(new PromotionEntity(), new PromotionEntity()));

        List<Promotion> result = promotionService.findAll();

        assertEquals(2, result.size());
        verify(promotionRepository).findAll();
    }
}
