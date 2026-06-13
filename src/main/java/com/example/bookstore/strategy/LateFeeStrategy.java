package com.example.bookstore.strategy;

import java.math.BigDecimal;

public interface LateFeeStrategy {

    BigDecimal calculateFee(long overdueDays);
}