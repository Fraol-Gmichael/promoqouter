package com.fraolgmichael.promoqouter.promotion.service.impl;

import com.fraolgmichael.promoqouter.common.exception.ResponseCodes;
import com.fraolgmichael.promoqouter.common.exception.ServiceException;
import com.fraolgmichael.promoqouter.common.service.CustomerSegment;
import com.fraolgmichael.promoqouter.product.service.Category;
import com.fraolgmichael.promoqouter.product.service.Product;
import com.fraolgmichael.promoqouter.promotion.PromotionMapper;
import com.fraolgmichael.promoqouter.promotion.dataaccess.PromotionRepository;
import com.fraolgmichael.promoqouter.promotion.dataaccess.PromotionSpecs;
import com.fraolgmichael.promoqouter.promotion.dto.CartRequestDto;
import com.fraolgmichael.promoqouter.promotion.service.Promotion;
import com.fraolgmichael.promoqouter.promotion.service.PromotionsWithProducts;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AvailablePromotionsFilterHelper {
    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;

    public static List<Promotion> filterPromotions(
            List<Promotion> promotions,
            CustomerSegment targetSegment
    ) {

        return promotions.stream().collect(
                Collectors.groupingBy(
                        p -> p.getName().toLowerCase(),
                        LinkedHashMap::new,
                        Collectors.toList()
                )).values().stream().map(group -> matchTargetSegment(targetSegment, group)).toList();
    }

    private static Promotion matchTargetSegment(CustomerSegment targetSegment, List<Promotion> group) {
        return group.stream()
                .filter(p -> p.getCustomerSegment() == targetSegment)
                .findFirst()
                .orElse(group.getFirst());
    }

    private static List<UUID> collectProductIds(CartRequestDto cartRequestDto) {
        return cartRequestDto.items().stream().map(CartRequestDto.CartItem::productId).distinct().toList();
    }

    private static void validateOutOfStock(List<CartRequestDto.CartItem> items, List<Product> products) {
        List<String> errors = new ArrayList<>();
        for (CartRequestDto.CartItem item : items) {
            products.stream().filter(p -> p.getId().equals(item.productId())).findFirst().ifPresent(
                    product -> {
                        if (product.getStock() < item.qty()) {
                            errors.add(String.format("product %s has insufficient stock", product.getName()));
                        }
                    }
            );
        }
        if (!errors.isEmpty()) {
            throw new ServiceException(ResponseCodes.CONFLICT, String.join(", ", errors));
        }
    }

    public PromotionsWithProducts retrievePromotionsForCart(
            CartRequestDto requestDto, Function<List<UUID>, List<Product>> productFetcher
    ) {
        List<UUID> productIds = collectProductIds(requestDto);
        List<Product> products = getProductsOrElseThrow(productIds, requestDto.items(), productFetcher);
        List<Category> categories = products.stream().map(Product::getCategory).distinct().toList();
        List<CustomerSegment> customerSegments = List.of(CustomerSegment.ALL, requestDto.customerSegment());
        List<Promotion.Status> statuses = List.of(Promotion.Status.ACTIVE);
        Sort sortOrder = Sort.by(Sort.Direction.ASC, "priority");
        List<Promotion> availablePromotions = promotionRepository.findAll(
                PromotionSpecs.applicable(productIds, categories, customerSegments, statuses, requestDto.promoCodes()),
                sortOrder
        ).stream().map(promotionMapper::fromEntityToPromotion).toList();
        return PromotionsWithProducts.builder()
                .promotions(filterPromotions(availablePromotions, requestDto.customerSegment()))
                .products(products).build();
    }

    private List<Product> getProductsOrElseThrow(
            List<UUID> productIds,
            List<CartRequestDto.CartItem> items,
            Function<List<UUID>, List<Product>> productFetcher
    ) {
        List<Product> products = productFetcher.apply(productIds);
        if (products.size() != productIds.size()) {
            throw new ServiceException(ResponseCodes.NOT_FOUND, "some products were not found");
        }
        validateOutOfStock(items, products);
        return products;
    }


}
