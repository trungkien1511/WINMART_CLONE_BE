package com.winmart.controller;

import com.winmart.dto.category.CategoryChildDto;
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

    @GetMapping("/{parentSlug}")
    public List<CategoryChildDto> getCategoryChild(@PathVariable String parentSlug) {
        return categoryService.getCategoryChild(parentSlug);
    }

    @GetMapping("/{parentSlug}/products")
    public List<ProductSummaryDto> getProductsByParentSlug(@PathVariable String parentSlug) {
        return productService.getProductsByParentSlug(parentSlug);
    }

    @GetMapping("/{parentSlug}/{childSlug}/products")
    public List<ProductSummaryDto> getProductSByChild(
            @PathVariable String parentSlug,
            @PathVariable String childSlug
    ) {
        return productService.getProductsByChildSlug(parentSlug, childSlug);
    }
}
