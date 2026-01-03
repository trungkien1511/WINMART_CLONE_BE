package com.winmart.category.dto;

import java.util.UUID;

public record CategoryChildDto(
        UUID id,
        String name,
        String slug
) {}
