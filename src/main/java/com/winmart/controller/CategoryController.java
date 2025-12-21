package com.winmart.controller;

import com.winmart.dto.category.CategoryTreeDto;
import com.winmart.dto.product.ProductSummaryDto;
import com.winmart.service.CategoryService;
import com.winmart.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    public CategoryController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
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
        return productService.getProductsByCategory(slug, order, brands);
    }

}
