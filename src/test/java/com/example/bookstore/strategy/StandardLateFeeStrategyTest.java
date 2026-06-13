package com.example.bookstore.strategy;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StandardLateFeeStrategyTest {

    private final StandardLateFeeStrategy strategy =
            new StandardLateFeeStrategy();

    @Test
    void shouldCalculateFeeForOverdueDays() {
        BigDecimal result = strategy.calculateFee(5);

        assertEquals(new BigDecimal("10.00"), result);
    }

    @Test
    void shouldReturnZeroWhenBookIsNotOverdue() {
        BigDecimal result = strategy.calculateFee(0);

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void shouldReturnZeroForNegativeDays() {
        BigDecimal result = strategy.calculateFee(-3);

        assertEquals(BigDecimal.ZERO, result);
    }
}