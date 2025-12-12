package com.winmart.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "brands",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_brands_code", columnNames = "code"),
                @UniqueConstraint(name = "uq_brands_slug", columnNames = "slug")
        }
)
public class Brand {

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

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "is_active")
    private Boolean is_active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

    // Constructors
    public Brand() {
    }

    // Constructor đầy đủ
    public Brand(String code, String name, String slug, String description, String logoUrl) {
        this.code = code;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.logoUrl = logoUrl;
    }

    // Constructor cơ bản (chỉ required fields)
    public Brand(String code, String name, String slug) {
        this.code = code;
        this.name = name;
        this.slug = slug;
    }

    // Constructor không có logo
    public Brand(String code, String name, String slug, String description) {
        this.code = code;
        this.name = name;
        this.slug = slug;
        this.description = description;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getDescription() {
        return description;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Boolean getIs_active() {
        return is_active;
    }

    // Setters
    public void setId(UUID id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void setIs_active(Boolean is_active) {
        this.is_active = is_active;
    }

    @Override
    public String toString() {
        return "Brand{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", description='" + description + '\'' +
                ", logoUrl='" + logoUrl + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", products=" + products +
                '}';
    }

    // equals và hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Brand brand = (Brand) o;
        return id != null && id.equals(brand.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
