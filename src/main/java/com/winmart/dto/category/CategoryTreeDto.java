package com.winmart.dto.category;
import java.util.UUID;
import java.util.List;

public record CategoryTreeDto(
        UUID id,
        String name,
        String slug,
        List<CategoryChildDto> children
)
{}