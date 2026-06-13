package com.example.bookstore.strategy;

import org.springframework.stereotype.Component;

@Component
public class LateFeeStrategyFactory {

    private final StandardLateFeeStrategy standardStrategy;
    private final ReducedLateFeeStrategy reducedStrategy;

    public LateFeeStrategyFactory(
            StandardLateFeeStrategy standardStrategy,
            ReducedLateFeeStrategy reducedStrategy
    ) {
        this.standardStrategy = standardStrategy;
        this.reducedStrategy = reducedStrategy;
    }

    public LateFeeStrategy getStrategy(FeePolicyType type) {
        return switch (type) {
            case STANDARD -> standardStrategy;
            case REDUCED -> reducedStrategy;
        };
    }
}