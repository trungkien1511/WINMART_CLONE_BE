package com.winmart.dto.product;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSummaryDto(
        UUID id,
        String name,
        String slug,
        BigDecimal finalPrice,
        BigDecimal originalPrice,
        String packagingType
) {
}

