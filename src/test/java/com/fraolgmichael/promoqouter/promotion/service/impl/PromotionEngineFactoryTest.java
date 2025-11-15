package com.fraolgmichael.promoqouter.promotion.service.impl;

import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromotionEngineFactoryTest {

    @Mock
    private PromotionEngine engine;

    private PromotionEngineFactory factory;

    @BeforeEach
    void setUp() {
        factory = new PromotionEngineFactory(List.of(engine));
    }

    @Test
    void getPromotionEngine() {
        when(engine.match(Promotion.Type.BUY_X_GET_Y)).thenReturn(true);
        Optional<PromotionEngine> promotionEngine = factory.getPromotionEngine(Promotion.Type.BUY_X_GET_Y);
        Assertions.assertTrue(promotionEngine.isPresent());
        Assertions.assertEquals(engine, promotionEngine.get());
    }

    @Test
    void getPromotionEngine_WhenNullType_ShouldReturnEmptyOptional() {
        Optional<PromotionEngine> promotionEngine = factory.getPromotionEngine(null);
        Assertions.assertTrue(promotionEngine.isEmpty());
    }


    @Test
    void getPromotionEngine_whenDoesNotMatch_ShouldReturnEmptyOptional() {
        Optional<PromotionEngine> promotionEngine = factory.getPromotionEngine(Promotion.Type.BUY_X_GET_Y);
        Assertions.assertTrue(promotionEngine.isEmpty());
    }
}