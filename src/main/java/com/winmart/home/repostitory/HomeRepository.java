// src/main/java/com/winmart/repository/HomeRepository.java
package com.winmart.home.repostitory;

import com.winmart.home.dto.HomeProductRow;
import com.winmart.product.domain.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HomeRepository extends JpaRepository<ProductCategory, UUID> {

    @Query("""
            SELECT NEW com.winmart.home.dto.HomeProductRow(
                COALESCE(parent.id, child.id),
                pp
            )
            FROM ProductCategory pc
            JOIN pc.category child
            LEFT JOIN child.parent parent
            JOIN pc.product p
            JOIN p.productPackaging pp
            JOIN pp.packagingType pt
            WHERE child.isActive = true
              AND p.isActive = true
              AND pp.isDefault = true
              AND (parent IS NULL OR parent.isActive = true)
            ORDER BY p.createdAt DESC
            """)
    List<HomeProductRow> findAllProductForHomeRaw();
}
