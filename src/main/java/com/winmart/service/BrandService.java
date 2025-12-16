package com.winmart.service;

import com.winmart.dto.BrandDto;
import com.winmart.repository.BrandRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public List<BrandDto> getBrandByCatSlug(String categorySlug) {
        return brandRepository.getBrandsByParentOrChildrenSlug(categorySlug);
    }
}
