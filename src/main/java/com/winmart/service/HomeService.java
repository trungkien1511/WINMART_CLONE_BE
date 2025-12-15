package com.winmart.service;

import com.winmart.dto.home.HomeCategorySectionDto;
import com.winmart.dto.home.HomeProductRow;
import com.winmart.dto.product.ProductSummaryDto;
import com.winmart.entity.Category;
import com.winmart.entity.ProductPackaging;
import com.winmart.repository.CategoryRepository;
import com.winmart.repository.HomeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class HomeService {

    private static final Logger log = LoggerFactory.getLogger(HomeService.class);

    private final HomeRepository homeRepository;
    private final CategoryRepository categoryRepository;
    private final PricingService pricingService;

    public HomeService(HomeRepository homeRepository, CategoryRepository categoryRepository, PricingService pricingService) {
        this.homeRepository = homeRepository;
        this.categoryRepository = categoryRepository;
        this.pricingService = pricingService;
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

        // Group products vào đúng category
        for (HomeProductRow row : rows) {
            UUID categoryId = row.categoryId();
            ProductPackaging pp = row.packaging();
            List<ProductSummaryDto> list = productsByCategory.get(categoryId);
            if (list == null) continue;


            var pricing = pricingService.getPricing(pp);

            ProductSummaryDto dto = new ProductSummaryDto(
                    pp.getId(),
                    pp.getProduct().getName(),
                    pp.getProduct().getSlug(),
                    pricing.finalPrice(),
                    pricing.displayOriginalPrice(),
                    pp.getPackagingType().getName()
            );

            list.add(dto);
        }


        // 4. Build result
        List<HomeCategorySectionDto> result = new ArrayList<>(parents.size());
        for (Category parent : parents) {
            result.add(new HomeCategorySectionDto(
                    parent.getId(),
                    parent.getName(),
                    parent.getSlug(),
                    productsByCategory.get(parent.getId())
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
                    List.of()
            ));
        }
        return result;
    }
}