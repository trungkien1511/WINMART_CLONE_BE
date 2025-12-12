package com.winmart.dto.product;

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
