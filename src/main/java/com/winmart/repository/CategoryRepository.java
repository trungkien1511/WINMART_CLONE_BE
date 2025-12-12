package com.winmart.repository;

import com.winmart.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
