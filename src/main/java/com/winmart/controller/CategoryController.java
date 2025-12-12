package com.winmart.controller;

import com.winmart.dto.category.CategoryTreeDto;
import com.winmart.service.CategoryService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/tree")
    public List<CategoryTreeDto> getCategoryTree() {
        return categoryService.getCategoryTree();
    }
}
