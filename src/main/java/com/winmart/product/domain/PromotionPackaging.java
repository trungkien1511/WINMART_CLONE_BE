package com.winmart.product.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "promotion_packagings")
public class PromotionPackaging {

    @EmbeddedId
    private PromotionPackagingId id;

    @ManyToOne
    @MapsId("promotionId")
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @ManyToOne
    @MapsId("packagingId")
    @JoinColumn(name = "packaging_id")
    private ProductPackaging packaging;
}

