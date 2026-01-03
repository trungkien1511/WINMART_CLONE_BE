package com.winmart.product.query.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductPackagingDto(
        UUID productPackagingId,
        String packagingTypeName,
        BigDecimal price,
        BigDecimal originalPrice,
        int stockQuantity,
        boolean stock,
        boolean onSale
) {
}
