package com.winmart.cart.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CartItemRow(
        UUID id,
        UUID cartId,
        UUID productPackagingId,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal discount,
        Instant createdAt,
        Instant updateAt
) {
}
