package com.winmart.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_products_code", columnNames = "code"),
                @UniqueConstraint(name = "uq_products_slug", columnNames = "slug")
        },
        indexes = {
                @Index(name = "idx_products_brands", columnList = "brand_id"),
                @Index(name = "idx_products_categories", columnList = "category_id")
        })
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"brand", "productPackaging", "productCategories"})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductCategory> productCategories = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id",
            foreignKey = @ForeignKey(name = "products_brand_id_fkey"))
    private Brand brand;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "slug", nullable = false, unique = true, length = 255)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductPackaging> productPackaging = new ArrayList<>();

    @Column(name = "sku")
    private String sku;

    // Builder pattern constructor
    @Builder
    public Product(String code, String name, String slug, String description,
                   BigDecimal basePrice, Brand brand, Boolean isActive) {
        this.code = code;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.basePrice = basePrice;
        this.brand = brand;
        this.isActive = isActive != null ? isActive : true;
    }

    // Helper methods
    public void addProductPackaging(ProductPackaging productPackaging) {
        this.productPackaging.add(productPackaging);
        productPackaging.setProduct(this);
    }

    public void removeProductPackaging(ProductPackaging productPackaging) {
        this.productPackaging.remove(productPackaging);
        productPackaging.setProduct(null);
    }

    public void addProductCategory(ProductCategory productCategory) {
        productCategories.add(productCategory);
        productCategory.setProduct(this);
    }

    public void removeProductCategory(ProductCategory productCategory) {
        productCategories.remove(productCategory);
        productCategory.setProduct(null);
    }

    // Constraint validation
    @PrePersist
    @PreUpdate
    private void validateConstraints() {
        if (basePrice != null && basePrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Base price must be non-negative");
        }
        if (code != null) {
            code = code.trim();
        }
        if (slug != null) {
            slug = slug.trim().toLowerCase();
        }
    }

    // Manual equals and hashCode implementation for JPA entities
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return id != null && id.equals(product.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


}