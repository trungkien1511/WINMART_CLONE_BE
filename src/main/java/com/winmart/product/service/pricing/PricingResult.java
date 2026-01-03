// com.winmart.product.service.pricing.PricingResult.java
package com.winmart.product.service.pricing;

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
