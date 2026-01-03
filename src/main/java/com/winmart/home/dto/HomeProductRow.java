// src/main/java/com/winmart/dto/home/HomeProductRow.java
package com.winmart.home.dto;

import com.winmart.product.domain.ProductPackaging;

import java.util.UUID;

public record HomeProductRow(
        UUID categoryId,          // id category gốc (parent hoặc child nếu không có parent)
        ProductPackaging packaging // entity ProductPackaging, bên trong có Product + PackagingType
) {
}
