package com.fraolgmichael.promoqouter.promotion.controller;

import com.fraolgmichael.promoqouter.promotion.dto.CreatePromotionRequestDto;
import com.fraolgmichael.promoqouter.promotion.dto.UpdatePromotionRequestDto;
import com.fraolgmichael.promoqouter.promotion.service.PromotionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PromotionControllerTest {
    @Mock
    PromotionService promotionService;
    @InjectMocks
    PromotionController promotionController;

    @Test
    void create() {
        promotionController.create(CreatePromotionRequestDto.builder().build());
        verify(promotionService).create(any());
    }

    @Test
    void update() {
        promotionController.update(UUID.randomUUID(), UpdatePromotionRequestDto.builder().build());
        verify(promotionService).update(any(), any());
    }

    @Test
    void get() {
        promotionController.get(UUID.randomUUID());
        verify(promotionService).get(any());
    }

    @Test
    void delete() {
        promotionController.delete(UUID.randomUUID());
        verify(promotionService).delete(any());
    }

    @Test
    void findAll() {
        promotionController.findAll();
        verify(promotionService).findAll();
    }

    @Test
    void findByName() {
        promotionController.findByName("test");
        verify(promotionService).findByName("test");
    }

}