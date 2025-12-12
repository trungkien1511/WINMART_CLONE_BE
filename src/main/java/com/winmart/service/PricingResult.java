// com.winmart.service.PricingResult.java
package com.winmart.service;

import java.math.BigDecimal;

public record PricingResult(
        BigDecimal finalPrice,
        BigDecimal displayOriginalPrice
) {

    public boolean isOnSale() {
        return displayOriginalPrice != null
                && finalPrice != null
                && displayOriginalPrice.compareTo(finalPrice) > 0;
    }

}
