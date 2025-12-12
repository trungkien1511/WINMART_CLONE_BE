package com.winmart.controller;

import com.winmart.dto.product.ProductDetailDto;
import com.winmart.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{slug}")
    public ProductDetailDto getProduct(@PathVariable String slug) {
        return productService.getProductBySlug(slug);
    }
}
