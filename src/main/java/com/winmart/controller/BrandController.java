package com.winmart.controller;

import com.winmart.dto.BrandDto;
import com.winmart.service.BrandService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/brand")
public class BrandController {
    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/by-category/{categorySlug}")
    public List<BrandDto> getBrandsByCategory(@PathVariable String categorySlug) {
        return brandService.getBrandByCatSlug(categorySlug);
    }


}
