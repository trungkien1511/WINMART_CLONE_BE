package com.winmart.service;

import com.winmart.dto.product.ProductDetailDto;
import com.winmart.dto.product.ProductPackagingDto;
import com.winmart.dto.product.ProductSummaryDto;
import com.winmart.entity.Category;
import com.winmart.entity.Product;
import com.winmart.entity.ProductCategory;
import com.winmart.entity.ProductPackaging;
import com.winmart.repository.ProductPackagingRepository;
import com.winmart.repository.ProductPackagingSpecs;
import com.winmart.repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductPackagingRepository productPackagingRepository;


    public ProductService(ProductRepository productRepository,
                          ProductPackagingRepository productPackagingRepository) {
        this.productRepository = productRepository;
        this.productPackagingRepository = productPackagingRepository;

    }

    private List<ProductSummaryDto> mapToSummary(List<ProductPackaging> pp) {
        return pp.stream().map(p -> {
            return new ProductSummaryDto(
                    p.getId(),
                    p.getProduct().getName(),
                    p.getProduct().getSlug(),
                    p.getFinalPrice(),
                    p.getOriginalPrice(),
                    p.getPackagingType().getName()
            );
        }).toList();
    }

    public ProductDetailDto getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug);

        List<ProductPackagingDto> packagingDtos = product.getProductPackaging()
                .stream()
                .map(pp -> {
                    return new ProductPackagingDto(
                            pp.getPackagingType().getId(),          // hoặc .toString()
                            pp.getPackagingType().getName(),
                            pp.getFinalPrice(),
                            pp.getOriginalPrice(),
                            pp.getStockQuantity(),
                            pp.isInStock(),
                            pp.isOnSale()
                    );
                })
                .toList();

        // 2. Lấy category con (child) của sản phẩm hiện tại
        List<ProductCategory> pcs = product.getProductCategories();
        if (pcs == null || pcs.isEmpty()) {
            // Không có category -> không có related
            return new ProductDetailDto(
                    product.getId(),
                    product.getName(),
                    product.getSku(),
                    packagingDtos,
                    List.of()  // relatedProducts
            );
        }

        ProductCategory pc = pcs.stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsPrimary()))
                .findFirst()
                .orElse(pcs.getFirst());   // fallback: lấy cái đầu tiên nếu không có primary

        Category child = pc.getCategory();
        UUID childCategoryId = child.getId();

        // 3. Lấy tối đa 5 packaging sản phẩm tương tự cùng cat con
        Pageable limit5 = PageRequest.of(0, 5);
        List<ProductPackaging> relatedPps =
                productPackagingRepository.findRelatedPackagingByChildCategory(childCategoryId,
                        product.getId(),
                        limit5);

        // 4. Map sang ProductSummaryDto
        List<ProductSummaryDto> relatedProducts = mapToSummary(relatedPps);

        // 5. Trả về Detail + related
        return new ProductDetailDto(
                product.getId(),
                product.getName(),
                product.getSku(),
                packagingDtos,
                relatedProducts
        );

    }

    private List<String> parseBrandSlugs(String brands) {
        if (brands == null || brands.isBlank()) {
            return List.of();
        }

        return Arrays.stream(brands.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();
    }

    private Sort parseSort(String order) {
        if (order == null || order.isBlank()) return Sort.unsorted();

        return switch (order) {
            case "price_asc" -> Sort.by("finalPrice").ascending();
            case "price_desc" -> Sort.by("finalPrice").descending();
            default -> Sort.unsorted();
        };
    }

    public List<ProductSummaryDto> getProductsByCategory(String slug, String order, String brands) {
        List<String> brandSlugs = parseBrandSlugs(brands);

        Sort sort = parseSort(order);

        var spec = ProductPackagingSpecs.distinct()
                .and(ProductPackagingSpecs.productActive())
                .and(ProductPackagingSpecs.categoryOrParentSlug(slug))
                .and(ProductPackagingSpecs.brandSlugIn(brandSlugs));

        var pp = productPackagingRepository.findAll(spec, sort);
        return mapToSummary(pp);
    }


}
