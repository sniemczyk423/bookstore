package com.example.bookstore.controller;

import com.example.bookstore.service.LateFeeService;
import com.example.bookstore.strategy.FeePolicyType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/late-fees")
public class LateFeeController {

    private final LateFeeService lateFeeService;

    public LateFeeController(LateFeeService lateFeeService) {
        this.lateFeeService = lateFeeService;
    }

    @GetMapping("/calculate")
    public BigDecimal calculateFee(
            @RequestParam long overdueDays,
            @RequestParam FeePolicyType policy
    ) {
        return lateFeeService.calculateFee(overdueDays, policy);
    }
}