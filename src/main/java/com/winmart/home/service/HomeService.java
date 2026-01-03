package com.winmart.home.service;

import com.winmart.category.domain.Category;
import com.winmart.category.repostitory.CategoryRepository;
import com.winmart.home.dto.HomeCategorySectionDto;
import com.winmart.home.dto.HomeProductRow;
import com.winmart.home.repostitory.HomeRepository;
import com.winmart.product.domain.ProductPackaging;
import com.winmart.product.query.dto.ProductSummaryDto;
import com.winmart.product.query.dto.PromotionRow;
import com.winmart.product.repository.PromotionPackagingRepository;
import com.winmart.product.util.ProductDtoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HomeService {

    private static final Logger log = LoggerFactory.getLogger(HomeService.class);

    private final HomeRepository homeRepository;
    private final CategoryRepository categoryRepository;
    private final PromotionPackagingRepository promoPkgRepo;
    private final ProductDtoMapper productDtoMapper;

    public HomeService(HomeRepository homeRepository, CategoryRepository categoryRepository, PromotionPackagingRepository promoPkgRepo, ProductDtoMapper productDtoMapper) {
        this.homeRepository = homeRepository;
        this.categoryRepository = categoryRepository;
        this.promoPkgRepo = promoPkgRepo;
        this.productDtoMapper = productDtoMapper;
    }

    /**
     * Tối ưu:
     * 1. Loại bỏ nested stream filter O(n*m) -> dùng HashMap O(n+m)
     * 2. Pre-allocate collections với capacity
     * 3. Cache kết quả
     * 4. ReadOnly transaction
     * 5. Early return nếu không có data
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "homeSections", unless = "#result.isEmpty()")
    public List<HomeCategorySectionDto> getHomeSections() {

        // 1. Lấy parent categories
        List<Category> parents = categoryRepository.findByParentIsNullAndIsActiveTrue();
        if (parents.isEmpty()) {
            log.debug("No parent categories found");
            return List.of();
        }

        // 2. Lấy tất cả products
        List<HomeProductRow> rows = homeRepository.findAllProductForHomeRaw();
        if (rows.isEmpty()) {
            log.debug("No products found for home");
            return buildEmptySections(parents);
        }

        // 3. Group products theo categoryId - O(n) thay vì O(n*m)
        Map<UUID, List<ProductSummaryDto>> productsByCategory =
                new HashMap<>(parents.size() * 2); // Load factor optimization

        // Pre-initialize lists cho tất cả parent categories
        for (Category parent : parents) {
            productsByCategory.put(parent.getId(), new ArrayList<>());
        }

        List<UUID> packagingIds = rows.stream()
                .map(r -> r.packaging().getId())
                .distinct()
                .toList();


        Map<UUID, List<PromotionRow>> promosByPackaging =
                promoPkgRepo.findValidPromotionsForPackagings(packagingIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                PromotionRow::packagingId
                        ));


        // Group products vào đúng category
        for (HomeProductRow row : rows) {
            UUID categoryId = row.categoryId();
            ProductPackaging pp = row.packaging();

            List<ProductSummaryDto> list = productsByCategory.get(categoryId);
            if (list == null) continue;

            List<PromotionRow> promos =
                    promosByPackaging.getOrDefault(pp.getId(), List.of());

            ProductSummaryDto dto = productDtoMapper.toProductSummaryDto(pp, promos);
            list.add(dto);

        }

        // 4. Build result
        List<HomeCategorySectionDto> result = new ArrayList<>(parents.size());
        for (Category parent : parents) {
            List<ProductSummaryDto> products = productsByCategory.get(parent.getId());

            result.add(new HomeCategorySectionDto(
                    parent.getId(),
                    parent.getName(),
                    parent.getSlug(),
                    products,
                    products.size()
            ));
        }


        return result;
    }

    /**
     * Helper method để build sections rỗng khi không có products
     */
    private List<HomeCategorySectionDto> buildEmptySections(List<Category> parents) {
        List<HomeCategorySectionDto> result = new ArrayList<>(parents.size());
        for (Category parent : parents) {
            result.add(new HomeCategorySectionDto(
                    parent.getId(),
                    parent.getName(),
                    parent.getSlug(),
                    List.of(),
                    0
            ));
        }
        return result;
    }
}