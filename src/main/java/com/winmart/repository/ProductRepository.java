package com.winmart.repository;

import com.winmart.entity.Product;
import com.winmart.entity.ProductPackaging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Product findBySlug(String slug);

    @Query("""
            SELECT pp
            FROM ProductCategory pc
            JOIN pc.category c
            JOIN pc.product p
            JOIN p.productPackagings pp
            WHERE c.isActive = true
              AND p.isActive = true
              AND pp.isDefault = true
              AND c.id = :categoryId
              AND p.id <> :productId
            ORDER BY p.createdAt DESC
            """)
    List<ProductPackaging> findRelatedPackagingsByChildCategory(UUID categoryId,
                                                                UUID productId,
                                                                org.springframework.data.domain.Pageable pageable);
}



