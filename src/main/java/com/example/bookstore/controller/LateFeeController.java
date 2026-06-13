package com.example.bookstore.controller;

import com.example.bookstore.service.LateFeeService;
import com.example.bookstore.strategy.FeePolicyType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/late-fees")
@Tag(
        name = "Late fees",
        description = "Late fee calculation operations"
)
public class LateFeeController {

    private final LateFeeService lateFeeService;

    public LateFeeController(LateFeeService lateFeeService) {
        this.lateFeeService = lateFeeService;
    }

    @Operation(
            summary = "Calculate a late fee",
            description = "Calculates a late fee based on the number of overdue days and the selected fee policy"
    )
    @GetMapping("/calculate")
    public BigDecimal calculateFee(
            @RequestParam long overdueDays,
            @RequestParam FeePolicyType policy
    ) {
        return lateFeeService.calculateFee(
                overdueDays,
                policy
        );
    }
}