package com.winmart.product.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "packaging_types",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_packaging_types_code", columnNames = "code"),
                @UniqueConstraint(name = "uq_packaging_types_name", columnNames = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"productPackagings"})
public class PackagingType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, unique = true, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "unit", length = 50)
    private String unit;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "packagingType", cascade = CascadeType.ALL)
    private List<ProductPackaging> productPackagings = new ArrayList<>();

    // Helper methods
    public void addProductPackaging(ProductPackaging productPackaging) {
        productPackagings.add(productPackaging);
        productPackaging.setPackagingType(this);
    }

    public void removeProductPackaging(ProductPackaging productPackaging) {
        productPackagings.remove(productPackaging);
        productPackaging.setPackagingType(null);
    }

    // equals và hashCode theo ID (chuẩn cho Entity)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PackagingType)) return false;
        PackagingType that = (PackagingType) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
