package com.winmart.service;

import com.winmart.entity.ProductPackaging;
import com.winmart.entity.Promotion;
import com.winmart.repository.PromotionPackagingRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PricingService {

    private final PromotionPackagingRepository promoPkgRepo;

    public PricingService(PromotionPackagingRepository promoPkgRepo) {
        this.promoPkgRepo = promoPkgRepo;
    }

    public PricingResult getPricing(ProductPackaging pp) {

        List<Promotion> promotions =
                promoPkgRepo.findValidPromotionsForPackaging(pp.getId());

        BigDecimal basePrice = pp.getPrice();          // giá bán hiện tại (không promo)
        BigDecimal bestPrice = basePrice;

        for (Promotion pr : promotions) {
            BigDecimal candidate = applyPromotion(basePrice, pr);
            if (candidate.compareTo(bestPrice) < 0) {
                bestPrice = candidate;
            }
        }


        BigDecimal finalPrice = bestPrice;

        // Tính originalPrice để hiển thị (giá gốc gạch ngang)
        BigDecimal displayOriginalPrice = getBigDecimal(pp, finalPrice, basePrice);

        return new PricingResult(finalPrice, displayOriginalPrice);
    }

    private BigDecimal applyPromotion(BigDecimal basePrice, Promotion pr) {
        BigDecimal price = basePrice;

        BigDecimal discountValue = pr.getDiscountValue();

        if ("percent".equals(pr.getDiscountType())) {
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
