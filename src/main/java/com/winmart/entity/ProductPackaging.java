package com.winmart.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "product_packagings",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_pp_barcode", columnNames = "barcode")
        },
        indexes = {
                @Index(name = "idx_pp_packaging_type", columnList = "packaging_type_id"),
                @Index(name = "idx_pp_product", columnList = "product_id"),
                @Index(name = "idx_pp_is_default", columnList = "is_default"),
                @Index(name = "ux_pp_product_packaging_type", columnList = "product_id, packaging_type_id")
        })
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"product", "packagingType"})
public class ProductPackaging {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false,
            foreignKey = @ForeignKey(name = "product_packagings_product_id_fkey"))
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "packaging_type_id", nullable = false,
            foreignKey = @ForeignKey(name = "product_packagings_packaging_type_id_fkey"))
    private PackagingType packagingType;


    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "final_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalPrice;

    @Column(name = "original_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    @Column(name = "barcode", unique = true, length = 255)
    private String barcode;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @OneToMany(mappedBy = "packaging")
    private Set<PromotionPackaging> promotionPackagings = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Builder constructor
    @Builder
    public ProductPackaging(Product product, PackagingType packagingType,
                            BigDecimal price, BigDecimal originalPrice,
                            Integer stockQuantity, String barcode, Boolean isDefault) {
        this.product = product;
        this.packagingType = packagingType;
        this.price = price;
        this.originalPrice = originalPrice;
        this.stockQuantity = stockQuantity != null ? stockQuantity : 0;
        this.barcode = barcode;
        this.isDefault = isDefault != null ? isDefault : false;
    }

    public boolean isOnSale() {
        return this.getOriginalPrice() != null
                && this.getOriginalPrice().compareTo(this.getFinalPrice()) > 0;
    }

    // Business logic methods
    public void markAsDefault() {
        this.isDefault = true;
    }

    public void unmarkAsDefault() {
        this.isDefault = false;
    }

    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }

    public boolean hasDiscount() {
        return originalPrice != null && price != null
                && originalPrice.compareTo(price) > 0;
    }

    public BigDecimal getDiscountAmount() {
        if (!hasDiscount()) {
            return BigDecimal.ZERO;
        }
        return originalPrice.subtract(price);
    }

    public BigDecimal getDiscountPercentage() {
        if (!hasDiscount()) {
            return BigDecimal.ZERO;
        }
        return getDiscountAmount()
                .divide(originalPrice, 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public void addStock(Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Quantity must be non-negative");
        }
        this.stockQuantity += quantity;
    }

    public void reduceStock(Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Quantity must be non-negative");
        }
        if (this.stockQuantity < quantity) {
            throw new IllegalStateException("Insufficient stock");
        }
        this.stockQuantity -= quantity;
    }

    // Validation
//    @PrePersist
//    @PreUpdate
//    private void validateConstraints() {
//        if (originPrice != null && originPrice.compareTo(BigDecimal.ZERO) < 0) {
//            throw new IllegalArgumentException("Origin price must be non-negative");
//        }
//        if (price != null && price.compareTo(BigDecimal.ZERO) < 0) {
//            throw new IllegalArgumentException("Price must be non-negative");
//        }
//        if (originalPrice != null && originalPrice.compareTo(BigDecimal.ZERO) < 0) {
//            throw new IllegalArgumentException("Original price must be non-negative");
//        }
//        if (stockQuantity != null && stockQuantity < 0) {
//            throw new IllegalArgumentException("Stock quantity must be non-negative");
//        }
//        if (barcode != null) {
//            barcode = barcode.trim();
//        }
//
//        // Business rule: originalPrice should be >= price
//        if (originalPrice != null && price != null && originalPrice.compareTo(price) < 0) {
//            throw new IllegalArgumentException("Original price cannot be less than selling price");
//        }
//    }

    // Manual equals and hashCode for JPA entities
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductPackaging)) return false;
        ProductPackaging that = (ProductPackaging) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}