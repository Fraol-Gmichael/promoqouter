package com.fraolgmichael.promoqouter.promotion.dataaccess;

import com.fraolgmichael.promoqouter.common.service.CustomerSegment;
import com.fraolgmichael.promoqouter.product.service.Category;
import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public class PromotionSpecs {

    public static Specification<PromotionEntity> applicable(
            List<UUID> productIds,
            List<Category> categories,
            List<CustomerSegment> segments,
            List<Promotion.Status> statuses,
            List<String> promoCodes
    ) {
        return (root, query, cb) -> {

            Predicate productMatch = cb.disjunction();
            if (productIds != null && !productIds.isEmpty()) {
                productMatch = cb.and(
                        cb.equal(root.get("target"), Promotion.Target.PRODUCT),
                        root.get("targetProductId").in(productIds)
                );
            }

            Predicate categoryMatch = cb.disjunction();
            if (categories != null && !categories.isEmpty()) {
                categoryMatch = cb.and(
                        cb.equal(root.get("target"), Promotion.Target.CATEGORY),
                        root.get("targetCategory").in(categories)
                );
            }

            Predicate qtyMatch = cb.equal(root.get("target"), Promotion.Target.QTY);

            Predicate promoCodeMatch = cb.disjunction();
            if (promoCodes != null && !promoCodes.isEmpty()) {
                promoCodeMatch = cb.and(
                        cb.equal(root.get("type"), Promotion.Type.PROMO_CODE),
                        root.get("promoCode").in(promoCodes)
                );
            }

            Predicate targetMatch = cb.or(productMatch, categoryMatch, qtyMatch, promoCodeMatch);

            Predicate segmentMatch = cb.conjunction();
            if (segments != null && !segments.isEmpty()) {
                segmentMatch = root.get("customerSegment").in(segments);
            }

            Predicate statusMatch = cb.conjunction();
            if (statuses != null && !statuses.isEmpty()) {
                statusMatch = root.get("status").in(statuses);
            }

            return cb.and(targetMatch, segmentMatch, statusMatch);
        };
    }
}
