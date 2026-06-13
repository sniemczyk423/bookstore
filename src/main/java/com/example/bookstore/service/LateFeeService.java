package com.example.bookstore.service;

import com.example.bookstore.strategy.FeePolicyType;
import com.example.bookstore.strategy.LateFeeStrategy;
import com.example.bookstore.strategy.LateFeeStrategyFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class LateFeeService {

    private final LateFeeStrategyFactory strategyFactory;

    public LateFeeService(LateFeeStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    public BigDecimal calculateFee(long overdueDays, FeePolicyType policyType) {
        LateFeeStrategy strategy = strategyFactory.getStrategy(policyType);

        return strategy.calculateFee(overdueDays);
    }
}