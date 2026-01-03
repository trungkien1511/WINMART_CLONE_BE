package com.winmart.cart.dto;

import java.util.List;

public record CartSnapshot(
        CartSummary summary,
        List<CartItemView> items
) {
}
