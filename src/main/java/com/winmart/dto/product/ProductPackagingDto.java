package com.winmart.dto.product;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductPackagingDto(
        UUID packagingTypeId,
        String packagingTypeName,
        BigDecimal price,
        BigDecimal originalPrice,
        int stockQuantity,
        boolean stock,
        boolean onSale
) {
}
