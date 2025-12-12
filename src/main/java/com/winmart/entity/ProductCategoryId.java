package com.winmart.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Composite primary key for ProductCategory entity.
 * Implements Serializable as required by JPA specification.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductCategoryId implements Serializable {


    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;


    public static ProductCategoryId of(UUID productId, UUID categoryId) {
        return new ProductCategoryId(productId, categoryId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductCategoryId)) return false;
        ProductCategoryId that = (ProductCategoryId) o;
        return Objects.equals(productId, that.productId)
                && Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, categoryId);
    }
}