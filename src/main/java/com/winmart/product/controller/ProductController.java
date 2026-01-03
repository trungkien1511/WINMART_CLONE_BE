package com.winmart.product.controller;

import com.winmart.product.query.ProductQueryService;
import com.winmart.product.query.dto.ProductDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductQueryService productQueryService;

    @GetMapping("/{slug}")
    public ProductDetailDto getProduct(@PathVariable String slug) {
        return productQueryService.getProductBySlug(slug);
    }
}
