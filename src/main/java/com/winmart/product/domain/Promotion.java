package com.winmart.product.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "promotions")
public class Promotion {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "discount_type")
    private String discountType; // percent | fixed

    @Column(name = "discount_value")
    private BigDecimal discountValue;

    private ZonedDateTime startAt;
    private ZonedDateTime endAt;

    private boolean isActive;

    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @OneToMany(mappedBy = "promotion")
    private List<PromotionPackaging> packagingTargets;
}

