package com.example.bookstore.service;

import com.example.bookstore.strategy.FeePolicyType;
import com.example.bookstore.strategy.LateFeeStrategy;
import com.example.bookstore.strategy.LateFeeStrategyFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LateFeeServiceTest {

    @Mock
    private LateFeeStrategyFactory strategyFactory;

    @Mock
    private LateFeeStrategy strategy;

    @InjectMocks
    private LateFeeService lateFeeService;

    @Test
    void shouldCalculateFeeUsingSelectedStrategy() {
        when(strategyFactory.getStrategy(FeePolicyType.STANDARD))
                .thenReturn(strategy);

        when(strategy.calculateFee(4))
                .thenReturn(new BigDecimal("8.00"));

        BigDecimal result = lateFeeService.calculateFee(
                4,
                FeePolicyType.STANDARD
        );

        assertEquals(new BigDecimal("8.00"), result);

        verify(strategyFactory).getStrategy(FeePolicyType.STANDARD);
        verify(strategy).calculateFee(4);
    }
}