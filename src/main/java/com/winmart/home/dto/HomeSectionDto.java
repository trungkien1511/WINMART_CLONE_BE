package com.winmart.home.dto;

import java.util.List;

public record HomeSectionDto(
        List<HomeCategorySectionDto> sections
) {
}
