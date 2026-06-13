package com.example.bookstore.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ReducedLateFeeStrategy implements LateFeeStrategy {

    private static final BigDecimal DAILY_FEE = new BigDecimal("1.00");

    @Override
    public BigDecimal calculateFee(long overdueDays) {
        if (overdueDays <= 0) {
            return BigDecimal.ZERO;
        }

        return DAILY_FEE.multiply(BigDecimal.valueOf(overdueDays));
    }
}