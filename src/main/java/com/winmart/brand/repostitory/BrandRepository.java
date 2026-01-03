package com.winmart.brand.repostitory;

import com.winmart.brand.domain.Brand;
import com.winmart.brand.dto.BrandDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {

    @Query("""
                select new com.winmart.brand.dto.BrandDto(
                    b.id,
                    b.name,
                    b.slug,
                    b.logoUrl
                )
                from Brand b
                join b.categories c
                where c.slug = :slug
                  and c.isActive = true
            """)
    List<BrandDto> getBrandsByParentOrChildrenSlug(@Param("slug") String slug);

}
