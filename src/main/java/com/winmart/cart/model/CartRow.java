package com.winmart.cart.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CartRow(
        UUID id,
        UUID userId,
        UUID guestId,
        boolean isActive,
        BigDecimal totalAmount,
        BigDecimal totalDiscount,
        UUID voucherId,
        Instant createdAt,
        Instant updatedAt
) {
}
