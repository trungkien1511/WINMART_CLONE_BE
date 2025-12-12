// src/main/java/com/winmart/dto/home/HomeProductRow.java
package com.winmart.dto.home;

import com.winmart.entity.ProductPackaging;

import java.util.UUID;

public record HomeProductRow(
        UUID categoryId,          // id category gốc (parent hoặc child nếu không có parent)
        ProductPackaging packaging // entity ProductPackaging, bên trong có Product + PackagingType
) {
}
