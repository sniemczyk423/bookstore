package com.example.bookstore.strategy;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReducedLateFeeStrategyTest {

    private final ReducedLateFeeStrategy strategy =
            new ReducedLateFeeStrategy();

    @Test
    void shouldCalculateReducedFee() {
        BigDecimal result = strategy.calculateFee(5);

        assertEquals(new BigDecimal("5.00"), result);
    }

    @Test
    void shouldReturnZeroWhenBookIsNotOverdue() {
        BigDecimal result = strategy.calculateFee(0);

        assertEquals(BigDecimal.ZERO, result);
    }
}