package com.fraolgmichael.promoqouter.promotion.dataaccess;

import com.fraolgmichael.promoqouter.common.service.CustomerSegment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PromotionRepository extends JpaRepository<PromotionEntity, UUID>, JpaSpecificationExecutor<PromotionEntity> {
    Optional<PromotionEntity> findByNameIgnoreCaseAndCustomerSegment(String name, CustomerSegment customerSegment);

    Long removeById(UUID id);

    List<PromotionEntity> findByNameIgnoreCase(String name);

    Optional<PromotionEntity> findByPromoCodeIgnoreCase(String promoCode);
}