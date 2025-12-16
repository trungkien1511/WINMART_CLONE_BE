package com.winmart.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_categories_code", columnNames = "code"),
                @UniqueConstraint(name = "uq_categories_slug", columnNames = "slug")
        },
        indexes = {
                @Index(name = "idx_categories_parent", columnList = "parent_id")
        })
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"parent", "children", "productCategories"})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "slug", nullable = false, unique = true, length = 255)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id",
            foreignKey = @ForeignKey(name = "categories_parent_id_fkey"))
    private Category parent;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> children = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductCategory> productCategories = new ArrayList<>();

    @ManyToMany(mappedBy = "categories")
    private List<Brand> brands;

    // Builder pattern constructor
    @Builder
    public Category(String code, String name, String slug, String description,
                    Category parent, Boolean isActive) {
        this.code = code;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.parent = parent;
        this.isActive = isActive != null ? isActive : true;
    }

    // Helper methods for bidirectional relationships
    public void addChild(Category child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(Category child) {
        children.remove(child);
        child.setParent(null);
    }

    public void addProductCategory(ProductCategory productCategory) {
        productCategories.add(productCategory);
        productCategory.setCategory(this);
    }

    public void removeProductCategory(ProductCategory productCategory) {
        productCategories.remove(productCategory);
        productCategory.setCategory(null);
    }

    // Business logic methods
    public boolean isRoot() {
        return parent == null;
    }

    public int getLevel() {
        int level = 0;
        Category current = this.parent;
        while (current != null) {
            level++;
            current = current.getParent();
        }
        return level;
    }

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    public boolean isDescendantOf(Category potentialAncestor) {
        if (potentialAncestor == null) {
            return false;
        }
        Category current = this.parent;
        while (current != null) {
            if (current.equals(potentialAncestor)) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    // Validation
    @PrePersist
    @PreUpdate
    private void validateConstraints() {
        if (code != null) {
            code = code.trim();
        }
        if (slug != null) {
            slug = slug.trim().toLowerCase();
        }
        // Prevent circular reference
        if (parent != null && (parent.equals(this) || parent.isDescendantOf(this))) {
            throw new IllegalArgumentException("Circular category hierarchy detected");
        }
    }

    // Manual equals and hashCode for JPA entities
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category category = (Category) o;
        return id != null && id.equals(category.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}