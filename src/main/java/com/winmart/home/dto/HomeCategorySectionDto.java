package com.winmart.home.dto;

import com.winmart.product.query.dto.ProductSummaryDto;

import java.util.List;
import java.util.UUID;

public record HomeCategorySectionDto(
        UUID id,
        String name,
        String slug,
        List<ProductSummaryDto> products,
        int quantityProduct

) {
}
