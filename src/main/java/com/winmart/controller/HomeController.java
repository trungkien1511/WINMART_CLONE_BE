package com.winmart.controller;

import com.winmart.dto.home.HomeCategorySectionDto;
import com.winmart.service.HomeService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/home")
public class HomeController {
    public HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping("/categories-section")
    public List<HomeCategorySectionDto> getHomeSections() {
        return homeService.getHomeSections();
    }
}
