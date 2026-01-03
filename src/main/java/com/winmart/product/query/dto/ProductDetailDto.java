package com.winmart.product.query.dto;

import java.util.List;
import java.util.UUID;

public record ProductDetailDto(
        UUID id,
        String name,
        String sku,
        List<ProductPackagingDto> productPackaging,
        List<ProductSummaryDto> relatedProducts
) {
}
