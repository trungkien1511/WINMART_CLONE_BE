package com.winmart.dto;

import java.util.UUID;

public record BrandDto(
        UUID id,
        String name,
        String slug,
        String logoUrl
) {
}

