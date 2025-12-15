package com.winmart.repository;

import com.winmart.dto.category.CategoryChildDto;
import com.winmart.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    // Lấy tất cả category cha (parent_id IS NULL)
    List<Category> findByParentIsNullAndIsActiveTrue();

    // Lấy tất cả category con (parent_id IS NOT NULL)
    List<Category> findByParentIsNotNullAndIsActiveTrue();

    List<Category> findByIsActiveTrue();

    @Query("""
                select (count(c) > 0)
                from Category c
                join c.parent p
                where c.slug = :childSlug
                  and p.slug = :parentSlug
                  and c.isActive = true
                  and p.isActive = true
            """)
    boolean existsBySlugAndParentSlug(@Param("childSlug") String childSlug,
                                      @Param("parentSlug") String parentSlug);

    @Query("""
            select NEW com.winmart.dto.category.CategoryChildDto(
                c.id,
                c.name,
                c.slug
            )
            from Category c
            join c.parent p
            where p.slug = :parentSlug
                and p.isActive = true
                and c.isActive = true
            """)
    List<CategoryChildDto> getCategoryChild(@Param("parentSlug") String parentSlug);
}
