package com.winmart.product.repository;

import com.winmart.product.domain.ProductPackaging;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductPackagingRepository
        extends JpaRepository<ProductPackaging, UUID>,
        JpaSpecificationExecutor<ProductPackaging> {

    @NonNull
    ProductPackaging getPackagingById(@NonNull UUID productPackagingId);


    @Query("""
            SELECT pp
            FROM ProductCategory pc
            JOIN pc.category c
            JOIN pc.product p
            JOIN p.productPackaging pp
            WHERE c.isActive = true
              AND p.isActive = true
              AND pp.isDefault = true
              AND c.id = :categoryId
              AND p.id <> :productId
            ORDER BY p.createdAt DESC
            """)
    List<ProductPackaging> findRelatedPackagingByChildCategory(UUID categoryId,
                                                               UUID productId,
                                                               org.springframework.data.domain.Pageable pageable);
}

