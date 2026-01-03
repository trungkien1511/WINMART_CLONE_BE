package com.winmart.category.service;

import com.winmart.category.repostitory.CategoryRepository;
import com.winmart.category.dto.CategoryChildDto;
import com.winmart.category.dto.CategoryTreeDto;
import com.winmart.category.domain.Category;
import com.winmart.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Tối ưu 1: Chỉ query 1 lần thay vì 2 lần
     * Tối ưu 2: Dùng HashMap với initialCapacity
     * Tối ưu 3: Dùng ArrayList thay vì stream collectors
     * Tối ưu 4: Cache kết quả nếu data ít thay đổi
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "categoryTree", unless = "#result.isEmpty()")
    public List<CategoryTreeDto> getCategoryTree() {
        // 1. Query 1 lần duy nhất để lấy tất cả categories
        List<Category> allCategories = categoryRepository.findByIsActiveTrue();

        if (allCategories.isEmpty()) {
            return List.of();
        }

        // 2. Phân loại và build map trong 1 vòng lặp
        List<Category> parents = new ArrayList<>();
        Map<UUID, List<CategoryChildDto>> childrenMap = new HashMap<>();

        for (Category category : allCategories) {
            if (category.getParent() == null) {
                parents.add(category);
                // Pre-init list để tránh getOrDefault
                childrenMap.put(category.getId(), new ArrayList<>());
            }
        }

        // 3. Map children vào parent
        for (Category category : allCategories) {
            if (category.getParent() != null) {
                UUID parentId = category.getParent().getId();
                List<CategoryChildDto> children = childrenMap.get(parentId);
                if (children != null) {
                    children.add(new CategoryChildDto(
                            category.getId(),
                            category.getName(),
                            category.getSlug()
                    ));
                }
            }
        }

        // 4. Build result
        List<CategoryTreeDto> result = new ArrayList<>(parents.size());
        for (Category parent : parents) {
            result.add(new CategoryTreeDto(
                    parent.getId(),
                    parent.getName(),
                    parent.getSlug(),
                    childrenMap.get(parent.getId())
            ));
        }

        log.debug("Built category tree with {} parents and {} total categories",
                parents.size(), allCategories.size());

        return result;
    }

    public CategoryTreeDto getCategoryTreeFromSlug(String slug) {
        Category current = categoryRepository.findBySlugWithParent(slug);
        if (current == null) {
            throw new NotFoundException("Category not found: " + slug);
        }

        // Nếu slug là con thì root = parent, nếu slug là cha thì root = chính nó
        UUID rootId = (current.getParent() != null) ? current.getParent().getId() : current.getId();

        Category root = categoryRepository.findParentWithChildren(rootId);
        if (root == null) {
            throw new NotFoundException("Root category not found");
        }

        List<CategoryChildDto> children = root.getChildren().stream()
                .filter(Category::getIsActive)
                .map(ch -> new CategoryChildDto(ch.getId(), ch.getName(), ch.getSlug()))
                .toList();

        return new CategoryTreeDto(root.getId(), root.getName(), root.getSlug(), children);
    }

}