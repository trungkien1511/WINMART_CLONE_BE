package com.winmart.product.service.pricing;

import com.winmart.product.domain.ProductPackaging;
import com.winmart.product.query.dto.PromotionRow;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PricingService {


    public PricingResult getPricing(ProductPackaging pp, List<PromotionRow> promotions) {

        BigDecimal basePrice = pp.getPrice(); // ✅ base
        BigDecimal bestPrice = basePrice;

        for (PromotionRow pr : promotions) {
            BigDecimal candidate = applyPromotion(basePrice, pr);
            if (candidate.compareTo(bestPrice) < 0) {
                bestPrice = candidate;
            }
        }

        BigDecimal displayOriginalPrice =
                bestPrice.compareTo(basePrice) < 0 ? basePrice : pp.getOriginalPrice();

        return new PricingResult(bestPrice, displayOriginalPrice);
    }


    private BigDecimal applyPromotion(BigDecimal basePrice, PromotionRow pr) {
        BigDecimal price = basePrice;

        BigDecimal discountValue = pr.discountValue();

        if ("percent".equals(pr.discountType())) {
            BigDecimal discountAmount = price
                    .multiply(discountValue)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            price = price.subtract(discountAmount);
        } else { // fixed
            price = price.subtract(discountValue);
        }

        // không cho giá âm
        return price.max(BigDecimal.ZERO);
    }


    private static BigDecimal getBigDecimal(ProductPackaging pp, BigDecimal finalPrice, BigDecimal basePrice) {
        BigDecimal displayOriginalPrice;

        boolean discounted = finalPrice.compareTo(basePrice) < 0;

        if (discounted) {
            // Có promo -> giá gốc là basePrice
            displayOriginalPrice = basePrice;
        } else if (pp.getOriginalPrice() != null
                && pp.getOriginalPrice().compareTo(basePrice) != 0) {
            // Không promo, nhưng originalPrice khác basePrice -> hiển thị
            displayOriginalPrice = pp.getOriginalPrice();
        } else {
            // Không có gì để gạch ngang
            displayOriginalPrice = null;
        }
        return displayOriginalPrice;
    }
}
