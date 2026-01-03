package com.winmart.product.domain;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class PromotionPackagingId implements Serializable {

    private UUID promotionId;
    private UUID packagingId;

    // equals(), hashCode()
}

