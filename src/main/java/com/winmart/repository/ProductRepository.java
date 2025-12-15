package com.winmart.repository;

import com.winmart.entity.Product;
import com.winmart.entity.ProductPackaging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Product findBySlug(String slug);


    @Query("""
                select distinct pp
                from ProductCategory pc
                join pc.category c
                join pc.product p
                join p.productPackaging pp
                where c.isActive = true
                  and p.isActive = true
                  and c.slug = :slug
            """)
    List<ProductPackaging> findPackagingByCategorySlug(@Param("slug") String slug);


    @Query("""
                select distinct pp
                from ProductCategory pc
                join pc.category c
                left join c.parent parent
                join pc.product p
                join p.productPackaging pp
                where c.isActive = true
                  and p.isActive = true
                  and (c.slug = :slug or parent.slug = :slug)
            """)
    List<ProductPackaging> findPackagingByParentOrChildrenSlug(@Param("slug") String slug);


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



