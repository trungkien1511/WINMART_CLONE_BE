package com.winmart.cart.service;

import com.winmart.cart.domain.CartOwner;
import com.winmart.cart.dto.CartSnapshot;
import com.winmart.cart.dto.CartSummary;
import com.winmart.cart.model.CartRow;
import com.winmart.cart.repository.CartItemRepositoryJdbc;
import com.winmart.cart.repository.CartRepositoryJdbc;
import com.winmart.product.repository.ProductPackagingRepository;
import com.winmart.product.repository.PromotionPackagingRepository;
import com.winmart.product.service.pricing.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepositoryJdbc cartRepo;
    private final CartItemRepositoryJdbc itemRepo;
    private final ProductPackagingRepository productPackagingRepo;
    private final PromotionPackagingRepository promoRepo;
    private final PricingService pricingService;

    @Transactional
    public CartSnapshot getCart(CartOwner owner) {
        var cart = getOrCreateCart(owner);
        return snapshot(cart.id());
    }

    @Transactional
    public CartSnapshot addItem(CartOwner owner, UUID productPackagingId, int qty) {
        if (qty <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "qty must be > 0");
        }

        var cart = getOrCreateCart(owner);


        var pp = productPackagingRepo.getPackagingById(productPackagingId);              // bạn implement
        var promotions = promoRepo.findValidPromotionsForPackaging(pp.getId());

        var pricing = pricingService.getPricing(pp, promotions);

        var bestPrice = pricing.finalPrice();               // bestPrice
        var displayOriginal = pricing.displayOriginalPrice();

        var unitPrice = (displayOriginal != null) ? displayOriginal : pp.getPrice();
        var discount = unitPrice.subtract(bestPrice);
        if (discount.compareTo(BigDecimal.ZERO) < 0) discount = BigDecimal.ZERO;

        itemRepo.upsertIncreaseQty(cart.id(), productPackagingId, qty, unitPrice, discount);
        cartRepo.recalcTotals(cart.id());

        return snapshot(cart.id());
    }

    @Transactional
    public CartSnapshot updateItemQty(CartOwner owner, UUID itemId, int qty) {
        var cart = getOrCreateCart(owner);

        if (qty <= 0) {
            itemRepo.deleteById(cart.id(), itemId); // delete nếu qty <= 0
        } else {
            int updated = itemRepo.updateQuantity(cart.id(), itemId, qty);
            if (updated == 0) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found");
            }
        }

        cartRepo.recalcTotals(cart.id());
        return snapshot(cart.id());
    }

    @Transactional
    public CartSnapshot removeItem(CartOwner owner, UUID itemId) {
        var cart = getOrCreateCart(owner);

        int deleted = itemRepo.deleteById(cart.id(), itemId);
        if (deleted == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found");
        }

        cartRepo.recalcTotals(cart.id());
        return snapshot(cart.id());
    }

    @Transactional
    public CartSnapshot clearCart(CartOwner owner) {
        var cart = getOrCreateCart(owner);

        itemRepo.clear(cart.id());
        cartRepo.recalcTotals(cart.id());

        return snapshot(cart.id());
    }

    // ----------------- helpers -----------------

    private CartRow getOrCreateCart(CartOwner owner) {
        return switch (owner) {
            case CartOwner.User u -> cartRepo.getOrCreateActiveCartForUser(u.userId());
            case CartOwner.Guest g -> cartRepo.getOrCreateActiveCartForGuest(g.guestId());
        };
    }

    private CartSnapshot snapshot(UUID cartId) {
        var cartRow = cartRepo.findById(cartId);
        var items = itemRepo.findViewsByCartId(cartId);

        var finalAmount = cartRow.totalAmount().subtract(cartRow.totalDiscount());

        return new CartSnapshot(
                new CartSummary(cartRow.totalAmount(), cartRow.totalDiscount(), finalAmount),
                items
        );
    }

}
