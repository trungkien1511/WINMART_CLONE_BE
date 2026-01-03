package com.winmart.cart.controller;

import com.winmart.auth.CustomUserDetails;
import com.winmart.cart.domain.CartOwner;
import com.winmart.cart.dto.CartSnapshot;
import com.winmart.cart.service.CartService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    public record AddCartItemRequest(UUID productPackagingId, int quantity) {
    }

    public record UpdateQtyRequest(int quantity) {
    }

    @GetMapping
    public CartSnapshot getCart(Authentication auth,
                                @CookieValue(value = "GUEST_ID", required = false) String guestId,
                                HttpServletResponse response) {
        var owner = resolveOwner(auth, guestId, response);
        return cartService.getCart(owner);
    }

    @PostMapping("/items")
    public CartSnapshot addItem(@RequestBody AddCartItemRequest req,
                                Authentication auth,
                                @CookieValue(value = "GUEST_ID", required = false) String guestId,
                                HttpServletResponse response) {
        var owner = resolveOwner(auth, guestId, response);
        return cartService.addItem(owner, req.productPackagingId(), req.quantity());
    }

    @PatchMapping("/items/{itemId}")
    public CartSnapshot updateQty(@PathVariable UUID itemId,
                                  @RequestBody UpdateQtyRequest req,
                                  Authentication auth,
                                  @CookieValue(value = "GUEST_ID", required = false) String guestId,
                                  HttpServletResponse response) {
        var owner = resolveOwner(auth, guestId, response);
        return cartService.updateItemQty(owner, itemId, req.quantity());
    }

    @DeleteMapping("/items/{itemId}")
    public CartSnapshot removeItem(@PathVariable UUID itemId,
                                   Authentication auth,
                                   @CookieValue(value = "GUEST_ID", required = false) String guestId,
                                   HttpServletResponse response) {
        var owner = resolveOwner(auth, guestId, response);
        return cartService.removeItem(owner, itemId);
    }

    @DeleteMapping("/items")
    public CartSnapshot clear(Authentication auth,
                              @CookieValue(value = "GUEST_ID", required = false) String guestId,
                              HttpServletResponse response) {
        var owner = resolveOwner(auth, guestId, response);
        return cartService.clearCart(owner);
    }

    // ----------------- resolve owner -----------------

    private CartOwner resolveOwner(Authentication auth,
                                   String guestId,
                                   HttpServletResponse response) {

        // 1) logged-in user
        if (auth != null
                && auth.isAuthenticated()
                && auth.getPrincipal() instanceof CustomUserDetails cud) {
            return new CartOwner.User(cud.getId());
        }

        // 2) guest
        UUID gid;
        if (guestId == null || guestId.isBlank()) {
            gid = UUID.randomUUID();
            CookieUtil.setGuestIdCookie(response, gid);
        } else {
            try {
                gid = UUID.fromString(guestId);
            } catch (IllegalArgumentException ex) {
                // cookie bẩn → reset
                gid = UUID.randomUUID();
                CookieUtil.setGuestIdCookie(response, gid);
            }
        }

        return new CartOwner.Guest(gid);
    }


    public static final class CookieUtil {
        private CookieUtil() {
        }

        public static void setGuestIdCookie(HttpServletResponse response, UUID guestId) {
            var cookie = new Cookie("GUEST_ID", guestId.toString());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 30); // 30 days
            // cookie.setSecure(true); // bật khi chạy HTTPS
            response.addCookie(cookie);
        }
    }
}
