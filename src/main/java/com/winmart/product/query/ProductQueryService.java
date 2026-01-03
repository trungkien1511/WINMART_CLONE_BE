package com.winmart.product.query;

import com.winmart.category.domain.Category;
import com.winmart.product.domain.Product;
import com.winmart.product.domain.ProductCategory;
import com.winmart.product.domain.ProductPackaging;
import com.winmart.product.query.dto.ProductDetailDto;
import com.winmart.product.query.dto.ProductPackagingDto;
import com.winmart.product.query.dto.ProductSummaryDto;
import com.winmart.product.query.dto.PromotionRow;
import com.winmart.product.query.spec.ProductPackagingSpecs;
import com.winmart.product.repository.ProductPackagingRepository;
import com.winmart.product.repository.ProductRepository;
import com.winmart.product.repository.PromotionPackagingRepository;
import com.winmart.product.service.pricing.PricingService;
import com.winmart.product.util.ProductDtoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ProductQueryService {

    private static final int RELATED_LIMIT = 5;
    private static final Logger log = LoggerFactory.getLogger(ProductQueryService.class);


    private final ProductRepository productRepository;
    private final ProductPackagingRepository productPackagingRepository;
    private final PricingService pricingService;
    private final PromotionPackagingRepository promoPkgRepo;
    private final ProductDtoMapper productDtoMapper;

    public ProductQueryService(ProductRepository productRepository,
                               ProductPackagingRepository productPackagingRepository,
                               PromotionPackagingRepository promoPkgRepo,
                               PricingService pricingService, ProductDtoMapper productDtoMapper) {
        this.productRepository = productRepository;
        this.productPackagingRepository = productPackagingRepository;
        this.promoPkgRepo = promoPkgRepo;
        this.pricingService = pricingService;
        this.productDtoMapper = productDtoMapper;
    }


    public ProductDetailDto getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug);

        var packagings = product.getProductPackaging();
        var ids = packagings.stream().map(ProductPackaging::getId).distinct().toList();

        var promosByPackaging = promoPkgRepo.findValidPromotionsForPackagings(ids)
                .stream()
                .collect(Collectors.groupingBy(PromotionRow::packagingId));

        List<ProductPackagingDto> packagingDtos = packagings.stream()
                .map(pp -> toPackagingDto(pp, promosByPackaging.getOrDefault(pp.getId(), List.of())))
                .toList();

        List<ProductSummaryDto> related = findRelatedProducts(product);

        return new ProductDetailDto(
                product.getId(),
                product.getName(),
                product.getSku(),
                packagingDtos,
                related
        );
    }

    public List<ProductSummaryDto> getProductsByCategory(String slug, String order, String brands) {
        log.info("getProductsByCategory slug={}, order={}, brands={}", slug, order, brands);

        var spec = ProductPackagingSpecs.distinct()
                .and(ProductPackagingSpecs.productActive())
                .and(ProductPackagingSpecs.categoryOrParentSlug(slug))
                .and(ProductPackagingSpecs.brandSlugIn(parseCsv(brands)));

//        Sort sort = parseSort(order);
        var packagings = productPackagingRepository.findAll(spec);

        // ← Dùng mapper chung
        return productDtoMapper.toProductSummaryDtos(packagings);
    }


    private List<ProductSummaryDto> findRelatedProducts(Product product) {
        UUID childCategoryId = getPrimaryChildCategoryId(product);
        if (childCategoryId == null) return List.of();

        var limit = PageRequest.of(0, RELATED_LIMIT);
        var packagings = productPackagingRepository
                .findRelatedPackagingByChildCategory(childCategoryId, product.getId(), limit);

        // ← Dùng mapper chung
        return productDtoMapper.toProductSummaryDtos(packagings);
    }


    private UUID getPrimaryChildCategoryId(Product product) {
        List<ProductCategory> pcs = product.getProductCategories();
        if (pcs == null || pcs.isEmpty()) return null;

        ProductCategory pc = pcs.stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsPrimary()))
                .findFirst()
                .orElse(pcs.getFirst());

        Category child = pc.getCategory();
        return child != null ? child.getId() : null;
    }

    private ProductPackagingDto toPackagingDto(ProductPackaging pp, List<PromotionRow> promos) {
        var pricing = pricingService.getPricing(pp, promos);
        return new ProductPackagingDto(
                pp.getId(),
                pp.getPackagingType().getName(),
                pricing.finalPrice(),
                pricing.displayOriginalPrice(),
                pp.getStockQuantity(),
                pp.isInStock(),
                pricing.isOnSale()
        );
    }


    private List<String> parseCsv(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        return Arrays.stream(csv.split(","))
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
            default -> {
                log.warn("Invalid order param: {}", order);
                yield Sort.unsorted();
            }
        };
    }

}
