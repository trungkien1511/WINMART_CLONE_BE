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
        BigDecimal price = basePrice;

        // TODO: nếu nhiều promotion, đây là chỗ bạn định nghĩa rule:
        // áp dụng cái đầu tiên, cái mạnh nhất, hay cộng dồn như trước.
        for (Promotion pr : promotions) {
            BigDecimal discountValue = pr.getDiscountValue();
            if ("percent".equals(pr.getDiscountType())) {
                BigDecimal discountAmount = price
                        .multiply(discountValue)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                price = price.subtract(discountAmount);
            } else { // fixed
                price = price.subtract(discountValue);
            }
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            price = BigDecimal.ZERO;
        }

        BigDecimal finalPrice = price;

        // Tính originalPrice để hiển thị (giá gốc gạch ngang)
        BigDecimal displayOriginalPrice;

        if (!promotions.isEmpty()) {
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

        return new PricingResult(finalPrice, displayOriginalPrice);
    }
}
