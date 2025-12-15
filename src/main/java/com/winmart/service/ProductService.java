package com.winmart.service;

import com.winmart.dto.product.ProductDetailDto;
import com.winmart.dto.product.ProductPackagingDto;
import com.winmart.dto.product.ProductSummaryDto;
import com.winmart.entity.Category;
import com.winmart.entity.Product;
import com.winmart.entity.ProductCategory;
import com.winmart.entity.ProductPackaging;
import com.winmart.exception.NotFoundException;
import com.winmart.repository.CategoryRepository;
import com.winmart.repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final PricingService pricingService;

    public ProductService(ProductRepository productRepository,
                          PricingService pricingService, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.pricingService = pricingService;
    }

    public ProductDetailDto getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug);


        List<ProductPackagingDto> packagingDtos = product.getProductPackaging()
                .stream()
                .map(pp -> {
                    var pricing = pricingService.getPricing(pp);
                    return new ProductPackagingDto(
                            pp.getPackagingType().getId(),          // hoặc .toString()
                            pp.getPackagingType().getName(),
                            pricing.finalPrice(),
                            pricing.displayOriginalPrice(),
                            pp.getStockQuantity(),
                            pp.isInStock(),
                            pricing.isOnSale()
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
                productRepository.findRelatedPackagingByChildCategory(childCategoryId,
                        product.getId(),
                        limit5);

        // 4. Map sang ProductSummaryDto
        List<ProductSummaryDto> relatedProducts = relatedPps.stream()
                .map(pp -> {
                    Product p = pp.getProduct();
                    var pricing = pricingService.getPricing(pp);

                    return new ProductSummaryDto(
                            pp.getId(),
                            p.getName(),
                            p.getSlug(),
                            pricing.finalPrice(),
                            pricing.displayOriginalPrice(),
                            pp.getPackagingType().getName()
                    );
                })
                .toList();

        // 5. Trả về Detail + related
        return new ProductDetailDto(
                product.getId(),
                product.getName(),
                product.getSku(),
                packagingDtos,
                relatedProducts
        );

    }

    public List<ProductSummaryDto> getProductsByParentSlug(String parentSlug) {
        var pp = productRepository.findPackagingByParentOrChildrenSlug(parentSlug);
        return mapToSummary(pp);
    }

    public List<ProductSummaryDto> getProductsByChildSlug(String parentSlug, String childSlug) {
        // validate child thuộc parent
        boolean ok = categoryRepository.existsBySlugAndParentSlug(childSlug, parentSlug);
        if (!ok) {
            throw new NotFoundException("Child category not found in parent category");
        }

        var pp = productRepository.findPackagingByCategorySlug(childSlug);
        return mapToSummary(pp);
    }

    private List<ProductSummaryDto> mapToSummary(List<ProductPackaging> pp) {
        return pp.stream().map(p -> {
            var pricing = pricingService.getPricing(p);
            return new ProductSummaryDto(
                    p.getId(),
                    p.getProduct().getName(),
                    p.getProduct().getSlug(),
                    pricing.finalPrice(),
                    pricing.displayOriginalPrice(),
                    p.getPackagingType().getName()
            );
        }).toList();
    }
}
