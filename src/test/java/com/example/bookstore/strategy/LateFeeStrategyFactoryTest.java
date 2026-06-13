package com.example.bookstore.strategy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class LateFeeStrategyFactoryTest {

    private final StandardLateFeeStrategy standardStrategy =
            new StandardLateFeeStrategy();

    private final ReducedLateFeeStrategy reducedStrategy =
            new ReducedLateFeeStrategy();

    private final LateFeeStrategyFactory factory =
            new LateFeeStrategyFactory(
                    standardStrategy,
                    reducedStrategy
            );

    @Test
    void shouldReturnStandardStrategy() {
        LateFeeStrategy result =
                factory.getStrategy(FeePolicyType.STANDARD);

        assertSame(standardStrategy, result);
    }

    @Test
    void shouldReturnReducedStrategy() {
        LateFeeStrategy result =
                factory.getStrategy(FeePolicyType.REDUCED);

        assertSame(reducedStrategy, result);
    }
}