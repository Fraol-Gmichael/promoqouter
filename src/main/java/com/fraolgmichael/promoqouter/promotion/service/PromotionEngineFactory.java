package com.fraolgmichael.promoqouter.promotion.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PromotionEngineFactory {
    private final List<PromotionEngine> engines;

    public Optional<PromotionEngine> getPromotionEngine(Promotion.Type type) {
        if (type == null) return Optional.empty();
        return engines.stream().filter(engine -> engine.match(type)).findFirst();
    }
}
