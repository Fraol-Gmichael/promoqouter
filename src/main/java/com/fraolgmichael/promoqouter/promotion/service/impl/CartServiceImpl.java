package com.fraolgmichael.promoqouter.promotion.service.impl;

import com.fraolgmichael.promoqouter.product.ProductMapper;
import com.fraolgmichael.promoqouter.product.dataaccess.ProductEntity;
import com.fraolgmichael.promoqouter.product.dataaccess.ProductRepository;
import com.fraolgmichael.promoqouter.product.service.Product;
import com.fraolgmichael.promoqouter.product.service.ProductService;
import com.fraolgmichael.promoqouter.promotion.dto.Cart;
import com.fraolgmichael.promoqouter.promotion.dto.CartRequestDto;
import com.fraolgmichael.promoqouter.promotion.service.CartService;
import com.fraolgmichael.promoqouter.promotion.service.PromotionEngineFactory;
import com.fraolgmichael.promoqouter.promotion.service.PromotionsWithProducts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.*;

@Service
@Validated
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final AvailablePromotionsFilterHelper availablePromotionsFilterHelper;
    private final PromotionEngineFactory promotionEngineFactory;
    private final ProductService productService;
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    private static CartRequestDto.CartItem getCartItem(List<CartRequestDto.CartItem> items, Product product) {
        return items.stream()
                .filter(cartItem -> cartItem.productId().equals(product.getId())).findFirst().orElseThrow();
    }

    @Override
    public Cart quote(CartRequestDto cartRequestDto) {
        PromotionsWithProducts promotionsWithProducts =
                availablePromotionsFilterHelper.retrievePromotionsForCart(cartRequestDto, productService::getProducts);
        return applyPromotionsToCart(cartRequestDto, promotionsWithProducts);
    }

    protected Cart initialCartResponseDtoFromRequestDto(
            List<Product> products,
            List<CartRequestDto.CartItem> items
    ) {
        Map<UUID, Cart.ProductDiscountInfo> discountInfoMap = new HashMap<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (Product product : products) {
            CartRequestDto.CartItem cartItem = getCartItem(items, product);

            Cart.ProductDiscountInfo discountInfo = Cart.ProductDiscountInfo.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .product(product)
                    .cartItem(cartItem)
                    .discounts(new ArrayList<>())
                    .build();

            discountInfoMap.put(product.getId(), discountInfo);

            totalPrice = totalPrice.add(calculateLineTotal(product, cartItem));
        }

        return Cart.builder().productDiscountInfos(discountInfoMap).totalPrice(totalPrice).build();
    }

    private BigDecimal calculateLineTotal(Product product, CartRequestDto.CartItem cartItem) {
        return product.getPrice().multiply(BigDecimal.valueOf(cartItem.qty()));
    }

    @Override
    @Transactional
    public Cart confirm(CartRequestDto cartRequestDto) {
        PromotionsWithProducts promotionsWithProducts =
                availablePromotionsFilterHelper.retrievePromotionsForCart(cartRequestDto, productService::getProductsForUpdate);
        Cart cart = applyPromotionsToCart(cartRequestDto, promotionsWithProducts);
        List<ProductEntity> toBeUpdated = new ArrayList<>();
        for (Cart.ProductDiscountInfo productDiscountInfo : cart.getProductDiscountInfos().values()) {
            var product = productDiscountInfo.getProduct();
            product.setStock(product.getStock() - productDiscountInfo.getCartItem().qty());
            toBeUpdated.add(productMapper.fromProductToEntity(product));
        }
        productRepository.saveAll(toBeUpdated);
        return cart;
    }

    protected Cart applyPromotionsToCart(CartRequestDto cartRequestDto, PromotionsWithProducts promotionsWithProducts) {
        return promotionsWithProducts.promotions().stream()
                .reduce(
                        initialCartResponseDtoFromRequestDto(promotionsWithProducts.products(), cartRequestDto.items()),
                        (currentCart, promotion) ->
                                promotionEngineFactory.getPromotionEngine(promotion.getType())
                                        .map(engine -> engine.apply(
                                                promotion,
                                                promotionsWithProducts.products(),
                                                cartRequestDto,
                                                currentCart
                                        )).orElse(currentCart),
                        (_, right) -> right
                );
    }
}
