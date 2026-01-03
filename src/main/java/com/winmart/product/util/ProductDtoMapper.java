package com.winmart.product.util;

import com.winmart.product.domain.ProductPackaging;
import com.winmart.product.query.dto.ProductSummaryDto;
import com.winmart.product.query.dto.PromotionRow;
import com.winmart.product.repository.PromotionPackagingRepository;
import com.winmart.product.service.pricing.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductDtoMapper {

    private final PricingService pricingService;
    private final PromotionPackagingRepository promoPkgRepo;

    /**
     * Batch fetch promotions và convert sang ProductSummaryDto
     */
    public List<ProductSummaryDto> toProductSummaryDtos(List<ProductPackaging> packagings) {
        if (packagings == null || packagings.isEmpty()) {
            return List.of();
        }

        // Batch fetch promotions
        Set<UUID> packagingIds = packagings.stream()
                .map(ProductPackaging::getId)
                .collect(Collectors.toSet());

        Map<UUID, List<PromotionRow>> promosByPackaging =
                fetchPromotionsGrouped(new ArrayList<>(packagingIds));

        // Convert to DTOs
        return packagings.stream()
                .map(pp -> toProductSummaryDto(pp, promosByPackaging.getOrDefault(pp.getId(), List.of())))
                .toList();
    }

    /**
     * Convert single ProductPackaging với promotions đã có sẵn
     */
    public ProductSummaryDto toProductSummaryDto(
            ProductPackaging pp,
            List<PromotionRow> promos
    ) {
        var pricing = pricingService.getPricing(pp, promos);
        return new ProductSummaryDto(
                pp.getId(),
                pp.getProduct().getName(),
                pp.getProduct().getSlug(),
                pricing.finalPrice(),
                pricing.displayOriginalPrice(),
                pp.getPackagingType().getName(),
                pricing.isOnSale()
        );
    }

    /**
     * Batch fetch promotions và group theo packaging ID
     */
    public Map<UUID, List<PromotionRow>> fetchPromotionsGrouped(List<UUID> packagingIds) {
        if (packagingIds == null || packagingIds.isEmpty()) {
            return Map.of();
        }

        return promoPkgRepo.findValidPromotionsForPackagings(packagingIds)
                .stream()
                .collect(Collectors.groupingBy(PromotionRow::packagingId));
    }
}