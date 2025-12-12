package com.winmart.dto.home;

import java.util.List;

public record HomeSectionDto(
        List<HomeCategorySectionDto> sections
){}
