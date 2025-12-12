package com.winmart.dto.home;

import com.winmart.dto.product.ProductSummaryDto;

import java.util.List;
import java.util.UUID;

public record HomeCategorySectionDto(
        UUID id,
        String name,
        String slug,
        List<ProductSummaryDto> products
) {
}
