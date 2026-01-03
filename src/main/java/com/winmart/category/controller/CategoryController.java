package com.winmart.category.controller;

import com.winmart.category.service.CategoryService;
import com.winmart.category.dto.CategoryTreeDto;
import com.winmart.product.query.ProductQueryService;
import com.winmart.product.query.dto.ProductSummaryDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductQueryService productQueryService;

    public CategoryController(CategoryService categoryService, ProductQueryService productQueryService) {
        this.categoryService = categoryService;
        this.productQueryService = productQueryService;
    }

    @GetMapping("/tree")
    public List<CategoryTreeDto> getCategoryTree() {
        return categoryService.getCategoryTree();
    }

    @GetMapping("/{slug}")
    public CategoryTreeDto getCategoryChild(@PathVariable String slug) {
        return categoryService.getCategoryTreeFromSlug(slug);
    }

    @GetMapping("/{slug}/products")
    public List<ProductSummaryDto> getProductsByCategory(
            @PathVariable String slug,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String brands
    ) {
        return productQueryService.getProductsByCategory(slug, order, brands);
    }

}
