package com.winmart.cart.dto;

import java.math.BigDecimal;

public record CartSummary(
        BigDecimal totalAmount,
        BigDecimal totalDiscount,
        BigDecimal finalAmount
) {
}
