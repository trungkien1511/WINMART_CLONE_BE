package com.winmart.cart.dto;

import com.winmart.product.query.dto.ProductPackagingDto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemView(
        UUID id,
        int quantity,

        BigDecimal snapshotUnitPrice,
        BigDecimal snapshotDiscount,
        BigDecimal snapshotFinalPrice,

        String productName,
        String productSlug,

        ProductPackagingDto packaging
) {
}
