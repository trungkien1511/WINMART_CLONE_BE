package com.winmart.product.query.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PromotionRow(
        UUID packagingId,
        String discountType,
        BigDecimal discountValue
) {
}

