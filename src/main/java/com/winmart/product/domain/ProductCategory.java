package com.winmart.product.domain;

import com.winmart.category.domain.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "product_categories",
        indexes = {
                @Index(name = "idx_product_categories_product", columnList = "product_id"),
                @Index(name = "idx_product_categories_category", columnList = "category_id"),
                @Index(name = "idx_product_categories_primary", columnList = "is_primary")
        })
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"product"})
public class ProductCategory {

    @EmbeddedId
    private ProductCategoryId id = new ProductCategoryId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_categories_product"))
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    @JoinColumn(name = "category_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_categories_category"))
    private Category category;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    // Constructor with relationships
    public ProductCategory(Product product, Category category) {
        this(product, category, false);
    }

    public ProductCategory(Product product, Category category, boolean isPrimary) {
        this.product = product;
        this.category = category;
        this.isPrimary = isPrimary;
        // MapsId automatically handles id synchronization
    }

    // Factory method for creating primary category assignment
    public static ProductCategory createPrimary(Product product, Category category) {
        return new ProductCategory(product, category, true);
    }

    // Business logic methods
    public void markAsPrimary() {
        this.isPrimary = true;
    }

    public void unmarkAsPrimary() {
        this.isPrimary = false;
    }

    // Validation
    @PrePersist
    @PreUpdate
    private void validateConstraints() {
        if (product == null) {
            throw new IllegalStateException("Product cannot be null");
        }
        if (category == null) {
            throw new IllegalStateException("Category cannot be null");
        }
    }

    // Manual equals and hashCode for JPA composite key entity
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductCategory)) return false;
        ProductCategory that = (ProductCategory) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}