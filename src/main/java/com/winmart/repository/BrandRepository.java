package com.winmart.repository;

import com.winmart.dto.BrandDto;
import com.winmart.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {

    @Query("""
                select NEW com.winmart.dto.BrandDto(
                    b.id,
                    b.name,
                    b.slug,
                    b.logoUrl
                )
                from Brand b
                join b.categories c
                left join c.parent parent
                where (c.slug = :slug or parent.slug = :slug)
                  and c.isActive = true
            """)
    List<BrandDto> getBrandsByParentOrChildrenSlug(@Param("slug") String slug);

}
